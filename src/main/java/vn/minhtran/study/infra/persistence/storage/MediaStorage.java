package vn.minhtran.study.infra.persistence.storage;

/***
 * This is targeted for accessing to object stored on Object Storage
 */
public interface MediaStorage {

	int countObject(String prefix);
}
