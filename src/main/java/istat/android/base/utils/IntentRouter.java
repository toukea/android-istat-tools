package istat.android.base.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import istat.android.base.interfaces.Decoder;
import istat.android.base.tools.ToolKits;

//TODO il faudrait peut être une interface pour definir la façon dont les intent sont lancé. afin de permettre des custom chooser dans certains cas.
public class IntentRouter {
    @Deprecated
    public final static String EXTRA_INTENT_URI = "intent_uri";
    public final static String EXTRA_INTENT_ACTION = "intent_action",
            EXTRA_INTENT_FLAGS = "intent_flags",
            EXTRA_INTENT_DATA = "intent_data",
            EXTRA_INTENT_DATA_TYPE = "intent_data_type",
            EXTRA_INTENT_TARGET_TYPE = "intent_target_type",
            EXTRA_INTENT_TARGET_CLASS = "intent_target_class",
            EXTRA_INTENT_TARGET_PACKAGE_NAME = "intent_target_package_name",
            EXTRA_INTENT_COMMAND = "intent_commands";
    public final static String INTENT_TARGET_TYPE_SERVICE_EXTRA = "service",
            INTENT_TARGET_TYPE_FOREGROUND_SERVICE_EXTRA = "foreground_service",
            INTENT_TARGET_TYPE_ACTIVITY_EXTRA = "activity",
            INTENT_ACTION_START_OWN_COMPONENT_EXTRA = "start_own_component",
            INTENT_ACTION_START_COMPONENT_EXTRA = "start_component",

    INTENT_ACTION_DEFAULT_EXTRA = "default";
    private Decoder<String, String> variableDecoder;


    private IntentRouter() {

    }

    //    private String getKeyNameValue(String keyName) {
//        return variableKeyName.get(keyName);
//    }

    public boolean routeIntent(Activity context) {
        return routeIntent(context, context.getIntent(), null);
    }

    public boolean routeIntent(Context context, Intent inputIntent) {
        return routeIntent(context, inputIntent, null);
    }

    public boolean routeIntent(Context context, Intent inputIntent, String prefixIntentTarget) {
        try {
            return routeIntentInternally(context, inputIntent, prefixIntentTarget);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean prepareIntentRouting(Context context, Intent inputIntent, String prefixIntentTarget) {
        if (inputIntent == null || context == null) {
            return false;
        }

        if (prefixIntentTarget != null) {
            Bundle bundle = computePrefixedStringBundle(prefixIntentTarget, inputIntent.getExtras());
            if (bundle != null) {
                inputIntent.replaceExtras(bundle);
            }
        }
        if (inputIntent.getExtras() != null) {
            String commandExpression = inputIntent.getExtras().getString(EXTRA_INTENT_COMMAND);
            if (!istat.android.base.tools.TextUtils.isEmpty(commandExpression)) {
                handleCommand(commandExpression);
            }
        }
        return true;
    }

    //TODO les itents pourait supporter aussi le passage des categories.
    private boolean routeIntentInternally(Context context, Intent inputIntent, String prefixIntentTarget) throws ActivityNotFoundException, SecurityException, ClassNotFoundException {
        if (!prepareIntentRouting(context, inputIntent, prefixIntentTarget)) {
            return false;
        }

        Intent intent = new Intent();
        if (!inputIntent.hasExtra(EXTRA_INTENT_ACTION)) {
            return routeIntentUri(context, inputIntent);
        }
        if (inputIntent.getExtras() != null) {
            intent.putExtras(inputIntent.getExtras());
        }
        String action = intent.getStringExtra(EXTRA_INTENT_ACTION);
        if (TextUtils.isEmpty(action)) {
            return routeIntentUri(context, inputIntent);
        }

        if (INTENT_ACTION_DEFAULT_EXTRA.equals(action)) {
            intent.setAction(Intent.ACTION_DEFAULT);
        } else {
            intent.setAction(action);
        }

        String targetType = INTENT_TARGET_TYPE_ACTIVITY_EXTRA;
        if (intent.getExtras() != null) {
            targetType = intent.getExtras().getString(EXTRA_INTENT_TARGET_TYPE, INTENT_TARGET_TYPE_ACTIVITY_EXTRA);
            if (INTENT_ACTION_START_OWN_COMPONENT_EXTRA.equals(action) || INTENT_ACTION_START_COMPONENT_EXTRA.equals(action)) {
                String className = intent.getStringExtra(EXTRA_INTENT_TARGET_CLASS);
                if (TextUtils.isEmpty(className)) {
                    return false;
                }
                if (INTENT_ACTION_START_OWN_COMPONENT_EXTRA.equals(action)) {
                    Class cLass = Class.forName(className);
                    intent.setClass(context, cLass);
                } else if (INTENT_ACTION_START_COMPONENT_EXTRA.equals(action)) {
                    String packageName = intent.getExtras().getString(EXTRA_INTENT_TARGET_PACKAGE_NAME, context.getPackageName());
                    intent.setClassName(packageName, className);
                }
            }
            fillIntentWithIntentEmbeddedData(intent, intent);
        }

        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        if (INTENT_TARGET_TYPE_SERVICE_EXTRA.equals(targetType)) {
            context.startService(intent);
        } else if (INTENT_TARGET_TYPE_FOREGROUND_SERVICE_EXTRA.equals(targetType)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                return false;
            }
        } else {
            context.startActivity(intent);
        }

        return true;
    }

    private void handleCommand(String commandExpression) {
        if (istat.android.base.tools.TextUtils.isEmpty(commandExpression)) {
            return;
        }
        List<Command> commands = parseCommandList(commandExpression.trim());
        Command.Handler handler;
        for (Command command : commands) {
            handler = nameCommandHandlerMap.get(command.getFunctionName());
            if (handler != null) {
                handler.onExecuteCommand(command);
            }
        }
    }

    private void fillIntentWithIntentEmbeddedData(Intent source, Intent target) {
        if (source == null || target == null) {
            return;
        }
        String dataUri = compute(source.getStringExtra(EXTRA_INTENT_DATA));
        String flags = compute(source.getStringExtra(EXTRA_INTENT_FLAGS));
        String action = compute(source.getStringExtra(EXTRA_INTENT_ACTION));
        if (target.getAction() == null) {
            if (action == null) {
                action = source.getAction();
            }
            if (action != null) {
                target.setAction(action);
            }
        }
        if (istat.android.base.tools.TextUtils.isEmpty(dataUri)) {
            dataUri = compute(source.getStringExtra(EXTRA_INTENT_URI));
        }
        if (!TextUtils.isEmpty(flags)) {
            int flagsInt = istat.android.base.tools.ToolKits.Word.parseInt(flags);
            target.setFlags(flagsInt);
        }
        String mimeType = compute(source.getStringExtra(EXTRA_INTENT_DATA_TYPE));
        if (!TextUtils.isEmpty(dataUri) && !TextUtils.isEmpty(mimeType)) {
            target.setDataAndType(Uri.parse(dataUri), mimeType);
        } else if (!TextUtils.isEmpty(dataUri)) {
            Uri data = Uri.parse(dataUri);
            target.setData(data);
        } else if (!TextUtils.isEmpty(mimeType)) {
            target.setType(mimeType);
        }
    }

    public boolean routeUri(Context context, String uriString) {
        return routeUri(context, Uri.parse(uriString));
    }

    public boolean routeUri(Context context, Uri uri) {
        return routeUri(context, uri, null, null);
    }

    public boolean routeUri(Context context, Uri uri, Bundle intentExtras) {
        return routeUri(context, uri, intentExtras, null);
    }

    public boolean routeUri(Context context, Uri uri, Bundle intentExtras, Intent modelIntent) {
        if (modelIntent == null) {
            modelIntent = new Intent();
        } else {
            modelIntent = new Intent(modelIntent);
        }
        if (intentExtras != null && !intentExtras.isEmpty()) {
            modelIntent.putExtras(intentExtras);
            if (uri != null) {
                modelIntent.removeExtra(EXTRA_INTENT_URI);
                modelIntent.removeExtra(EXTRA_INTENT_DATA);
            }
        }
        if (uri != null) {
            uri = Uri.parse(compute(uri.toString()));
            if (!TextUtils.isEmpty(uri.getFragment())) {
                modelIntent.setData(Uri.parse(compute(uri.getFragment())));
            }
            for (String paramKey : uri.getQueryParameterNames()) {
                //Il est juste a noter que les paramettre retourné ici on subit un URLDecode.
                modelIntent.putExtra(paramKey, compute(uri.getQueryParameter(paramKey)));
            }
            fillIntentWithIntentEmbeddedData(modelIntent, modelIntent);
            if (!prepareIntentRouting(context, modelIntent, null)) {
                return false;
            }

            for (UriIntentRoutHandler handler : uriIntentRoutHandlers) {
                if (!handler.handle(context, uri, modelIntent)) {
                    continue;
                }
                if (!(context instanceof Activity)) {
                    modelIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                try {
                    if (modelIntent.getAction() != null || modelIntent.getData() != null || modelIntent.getComponent() != null) {
                        context.startActivity(modelIntent);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean routeIntentUri(Context context, Intent intentInput) {
        Intent intent = new Intent(intentInput);
        Uri uri = intent.getData();
        if (uri == null) {
            fillIntentWithIntentEmbeddedData(intent, intent);
        }
        uri = intent.getData();
        return routeUri(context, uri, null, intent);
    }

    private String compute(String original) {
        if (variableDecoder == null || TextUtils.isEmpty(original)) {
            return original;
        }
        Pattern pattern = Pattern.compile("\\$\\{[\\w\\.]*\\}");
        Matcher matcher = pattern.matcher(original);
        String variableBlock;

        while (matcher.find()) {
            variableBlock = matcher.group();
            try {
                original = original.replace(variableBlock, variableDecoder.decode(variableBlock.replaceAll("^(\\$\\{)|(\\}$)", "")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return original;
    }

    private Bundle computePrefixedStringBundle(String prefix, Bundle bundle) {
        Bundle out = new Bundle();
        String value;
        for (String key : bundle.keySet()) {
            value = bundle.get(key) != null ? bundle.get(key) + "" : null;
            if (!TextUtils.isEmpty(value) && key.startsWith(prefix)) {
                out.putString(key.replaceFirst(prefix, ""), compute(value));
            }
        }

        return out;
    }

    public static Bundle createPrefixedStringBundle(String prefix, Bundle bundle) {
        Bundle out = new Bundle();
        String value;
        for (String key : bundle.keySet()) {
            value = bundle.get(key) != null ? bundle.get(key) + "" : null;
            if (!TextUtils.isEmpty(value) && key.startsWith(prefix)) {
                out.putString(key.replaceFirst(prefix, ""), value);
            }
        }

        return out;
    }

    public static class Builder {
        IntentRouter router = new IntentRouter();

//        public Builder setKeyNameValue(String keyName, String nameValue) {
//            this.router.variableKeyName.put(keyName, nameValue);
//            return this;
//        }

        public Builder addUriIntentRoutHandler(UriIntentRoutHandler resolver) {
            if (!this.router.uriIntentRoutHandlers.contains(resolver)) {
                this.router.uriIntentRoutHandlers.add(resolver);
            }
            return this;
        }

        public Builder putCommandHandler(String commandName, Command.Handler handler) {
            this.router.nameCommandHandlerMap.put(commandName, handler);
            return this;
        }

        public Builder setVariableDecoder(Decoder<String, String> variableDecoder) {
            this.router.variableDecoder = variableDecoder;
            return this;
        }


        public IntentRouter create() {
            return router;
        }

    }

    List<UriIntentRoutHandler> uriIntentRoutHandlers = new ArrayList();
    HashMap<String, Command.Handler> nameCommandHandlerMap = new HashMap<>();

    public interface UriIntentRoutHandler {
        boolean handle(Context context, Uri uri, Intent intent);
    }

    public static class Command {
        String functionName;
        String[] vars;

        public String[] getVars() {
            return vars;
        }

        public String getVar(int index) {
            return getVar(index, null);
        }

        public String getVar(int index, String defaultValue) {
            if (vars.length <= index) {
                return defaultValue;
            }
            return ToolKits.Word.parseString(vars[index]);
        }

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public void setVars(String... vars) {
            this.vars = vars;
        }

        public void setVarArray(String[] vars) {
            this.vars = vars;
        }

        public void setVarList(List<String> vars) {
            this.vars = vars.toArray(new String[vars.size()]);
            System.out.println("" + vars);
        }

        public interface Handler {
            void onExecuteCommand(Command command);
        }
    }

    public static class MapUriIntentRoutHandler implements UriIntentRoutHandler {
        HashMap<String, Class<? extends Activity>> uriClassMap = new HashMap();

        @Override
        public boolean handle(Context context, Uri uri, Intent intent) {
            if (uri == null || intent == null) {
                return false;
            }
            Class<? extends Activity> cLass = uriClassMap.get(uri.toString().replaceFirst("\\?.*", ""));
            intent.setClass(context, cLass);
            return true;
        }

        public void put(String uri, Class<? extends Activity> cLass) {
            uriClassMap.put(uri, cLass);
        }
    }

    public static abstract class DynamicPathIntentRoutHandler implements UriIntentRoutHandler {
        final HashMap<String, String> activityClassNameMap = new HashMap<>();

        public DynamicPathIntentRoutHandler(Context context) {
            if (context != null) {
                try {
                    PackageManager pm = context.getPackageManager();
                    PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
                    String[] splits;
                    for (ActivityInfo info : packageInfo.activities) {
                        splits = info.name.split("\\.");
                        if (splits.length >= 2) {
                            activityClassNameMap.put(splits[splits.length - 1], info.name);
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean handle(Context context, Uri uri, Intent intent) {
            if (uri == null || intent == null || !canResolve(uri) || uri.getLastPathSegment() == null) {
                return false;
            }
            String activityClassFullName = activityClassNameMap.get(uri.getLastPathSegment());
            if (activityClassFullName != null) {
                try {
                    Class<?> cLass = Class.forName(activityClassFullName);
                    intent.setClass(context, cLass);
                    return true;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        protected abstract boolean canResolve(Uri uri);
    }

    public static class UriLauncherIntentRoutHandler implements UriIntentRoutHandler {
        final String scheme, rootPath;

        public UriLauncherIntentRoutHandler(String scheme, String rootPath) {
            this.scheme = scheme;
            this.rootPath = rootPath;
        }

        @Override
        public boolean handle(Context context, Uri uri, Intent intent) {
            if (scheme.equals(uri.getScheme())) {
                if (rootPath == null || rootPath.equals(uri.getAuthority())) {
                    String action = uri.getLastPathSegment();
                    String pathFragment = uri.getFragment();
                    if (!TextUtils.isEmpty(pathFragment)) {
                        intent.setAction(TextUtils.isEmpty(action) ? Intent.ACTION_DEFAULT : action);
                        intent.setData(Uri.parse(pathFragment));
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static class PrefixDynamicPathIntentRoutHandler extends DynamicPathIntentRoutHandler {

        private final String prefix;

        public PrefixDynamicPathIntentRoutHandler(String prefix, Context context) {
            super(context);
            this.prefix = prefix;
        }

        @Override
        protected boolean canResolve(Uri uri) {
            return uri != null && uri.toString().startsWith(prefix);
        }
    }

    public static class PatternDynamicPathIntentRoutHandler extends DynamicPathIntentRoutHandler {
        String pattern;

        public PatternDynamicPathIntentRoutHandler(String pattern, Context context) {
            super(context);
            this.pattern = pattern;
        }

        @Override
        protected boolean canResolve(Uri uri) {
            return uri != null && uri.toString().matches(pattern);
        }
    }

    private List<Command> parseCommandList(String commandListString) {
        List<String> commandStringList = extractCommandExpression(commandListString);
        List<Command> commandEntities = new ArrayList<>();
        for (String command : commandStringList) {
            commandEntities.add(parseCommand(command));
        }
        return commandEntities;
    }

    private IntentRouter.Command parseCommand(String commands) {
        IntentRouter.Command command = new IntentRouter.Command();
        Pattern pattern = Pattern.compile("(\\w+\\d*)(\\(.*\\))");
        Matcher matcher = pattern.matcher(commands);
        while (matcher.find()) {
            command.setFunctionName(matcher.group(1));
            command.setVarList(parseVars(matcher.group(2)));
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
        return command;
    }

    private List<String> parseVars(String vars) {
        String[] splits = vars.split("('\\s?,\\s?')|(\\(')|('\\))");
        List<String> out = Arrays.asList(splits);
        return out.subList(1, out.size());
    }

    public List<String> extractCommandExpression(String commandString) {
        List<String> commands = new ArrayList<>();
        String[] splits = commandString.split("'\\);\\s?");
        for (String s : splits) {
            commands.add(s + "')");
        }
        return commands;
    }
}
