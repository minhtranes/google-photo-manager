package vn.minhtran.study.infra.persistence.storage;

import java.io.InputStream;

/***
 * This is targeted for accessing to object stored on Object Storage
 */
public interface MediaStorage {

	int countObject(String prefix);

	void putObject(String key, InputStream is) throws Exception;

	void makeBucket(String bucket);
}
