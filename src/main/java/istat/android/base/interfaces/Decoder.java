package istat.android.base.interfaces;

/**
 * Created by istat on 22/11/17.
 */

public interface Decoder<Entity, Encoded> {

    Entity decode(Encoded encoded) throws Exception;

}
