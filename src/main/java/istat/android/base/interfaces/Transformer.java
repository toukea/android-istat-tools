package istat.android.base.interfaces;

/**
 * Created by istat on 22/11/17.
 */

public interface Transformer<Entity, Encoded> {

    Entity transform(Encoded encoded) throws Exception;

}
