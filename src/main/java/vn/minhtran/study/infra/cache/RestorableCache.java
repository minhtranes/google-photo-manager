package vn.minhtran.study.infra.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.data.jpa.repository.JpaRepository;

public abstract class RestorableCache<K extends Serializable, V extends KeyEntity<K>> {

	private Map<K, V> store = new ConcurrentHashMap<>();

	protected V put(V value) {
		store.put(value.getKey(), value);
		executor.execute(() -> {
			getRepository().save(value);
		});
		return value;
	}

	protected boolean exist(K key) {
		return store.containsKey(key);
	}

	protected abstract JpaRepository<V, K> getRepository();
	private Executor executor = Executors.newFixedThreadPool(1);

	protected void restore() {
		List<V> all = getRepository().findAll();
		all.forEach(s -> {
			store.put(s.getKey(), s);
		});
	}

	protected V get(K key) {
		return store.get(key);
	}

}
