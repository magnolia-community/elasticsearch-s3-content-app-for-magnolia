package info.magnolia.forge.universalcontent.app.generic.others;

import java.util.List;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.StateTransitionException;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.ui.table.CustomResultSet;
import info.magnolia.forge.universalcontent.app.generic.ui.table.RowId;
import info.magnolia.forge.universalcontent.app.generic.ui.table.RowItem;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class CacheHelper<D> {

	private CacheManager cacheManager;

	public CacheHelper() {
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
		try {
			cacheManager.init();
		} catch (StateTransitionException e) {
			log.error("CacheHelper:", e);
		}
		cacheManager.createCache(GenericConstants.CACHED_RESULT_KEY, CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class, CustomResultSet.class, ResourcePoolsBuilder.heap(10)));
		cacheManager.createCache(GenericConstants.CACHED_ITEMS_KEY, CacheConfigurationBuilder
				.newCacheConfigurationBuilder(RowId.class, RowItem.class, ResourcePoolsBuilder.heap(10)));
		cacheManager.createCache(GenericConstants.CACHED_FACTORY_CONVERT_STRING_KEY, CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class, Params.class, ResourcePoolsBuilder.heap(10)));
		cacheManager.createCache(GenericConstants.CACHED_CONVERTER_CLASS_KEY, CacheConfigurationBuilder
				.newCacheConfigurationBuilder(Class.class, List.class, ResourcePoolsBuilder.heap(10)));
		cacheManager.createCache(GenericConstants.CACHED_SUBTYPE_OF_CLASS_KEY, CacheConfigurationBuilder
				.newCacheConfigurationBuilder(Class.class, List.class, ResourcePoolsBuilder.heap(10)));
	}

	public Boolean containsConverterClassKey(Class key) {
		Cache<Class, List> cacheResults = cacheManager.getCache(GenericConstants.CACHED_CONVERTER_CLASS_KEY,
				Class.class, List.class);
		if (cacheResults != null) {
			return cacheResults.containsKey(key);
		}
		return false;
	}

	public List getConverterClass(Class key) {
		Cache<Class, List> cacheResults = cacheManager.getCache(GenericConstants.CACHED_CONVERTER_CLASS_KEY,
				Class.class, List.class);
		if (cacheResults.containsKey(key)) {
			return cacheResults.get(key);
		}
		return null;
	}

	public void putConverterClass(Class key, List customResultSet) {
		Cache<Class, List> cacheResults = cacheManager.getCache(GenericConstants.CACHED_CONVERTER_CLASS_KEY,
				Class.class, List.class);
		cacheResults.put(key, customResultSet);
	}

	public void removeAllConverterClass() {
		Cache<Class, List> cachedItems = cacheManager.getCache(GenericConstants.CACHED_CONVERTER_CLASS_KEY, Class.class,
				List.class);
		cachedItems.clear();
	}

	public Boolean containsFactoryConvertKey(String key) {
		Cache<String, Params> cacheResults = cacheManager.getCache(GenericConstants.CACHED_FACTORY_CONVERT_STRING_KEY,
				String.class, Params.class);
		if (cacheResults != null) {
			return cacheResults.containsKey(key);
		}
		return false;
	}

	public Params getFactoryConvert(String key) {
		Cache<String, Params> cacheResults = cacheManager.getCache(GenericConstants.CACHED_FACTORY_CONVERT_STRING_KEY,
				String.class, Params.class);
		if (cacheResults.containsKey(key)) {
			return cacheResults.get(key);
		}
		return null;
	}

	public void putFactoryConvert(String key, Params customResultSet) {
		Cache<String, Params> cacheResults = cacheManager.getCache(GenericConstants.CACHED_FACTORY_CONVERT_STRING_KEY,
				String.class, Params.class);
		cacheResults.put(key, customResultSet);
	}

	public void removeAllFactoryConvert() {
		Cache<String, Params> cachedItems = cacheManager.getCache(GenericConstants.CACHED_FACTORY_CONVERT_STRING_KEY,
				String.class, Params.class);
		cachedItems.clear();
	}

	public Boolean containsCachedResultsKey(String key) {
		Cache<String, CustomResultSet> cacheResults = cacheManager.getCache(GenericConstants.CACHED_RESULT_KEY,
				String.class, CustomResultSet.class);
		if (cacheResults != null) {
			return cacheResults.containsKey(key);
		}
		return false;
	}

	public CustomResultSet getCachedResults(String key) {
		Cache<String, CustomResultSet> cacheResults = cacheManager.getCache(GenericConstants.CACHED_RESULT_KEY,
				String.class, CustomResultSet.class);
		if (cacheResults.containsKey(key)) {
			return cacheResults.get(key);
		}
		return null;
	}

	public void putCachedResults(String key, CustomResultSet customResultSet) {
		Cache<String, CustomResultSet> cacheResults = cacheManager.getCache(GenericConstants.CACHED_RESULT_KEY,
				String.class, CustomResultSet.class);
		cacheResults.put(key, customResultSet);
	}

	public void removeCachedResults(String key) {
		Cache<String, CustomResultSet> cacheResults = cacheManager.getCache(GenericConstants.CACHED_RESULT_KEY,
				String.class, CustomResultSet.class);
		cacheResults.remove(key);
	}

	public void removeAllCachedResults() {
		Cache<String, CustomResultSet> cacheResults = cacheManager.getCache(GenericConstants.CACHED_RESULT_KEY,
				String.class, CustomResultSet.class);
		cacheResults.clear();
	}

	public Boolean containsItemsKey(RowId key) {
		Cache<RowId, RowItem> cacheResults = cacheManager.getCache(GenericConstants.CACHED_ITEMS_KEY, RowId.class,
				RowItem.class);
		if (cacheResults != null) {
			return cacheResults.containsKey(key);
		}
		return false;
	}

	public RowItem getCachedItems(RowId key) {
		Cache<RowId, RowItem> cachedItems = cacheManager.getCache(GenericConstants.CACHED_ITEMS_KEY, RowId.class,
				RowItem.class);
		if (cachedItems.containsKey(key)) {
			return cachedItems.get(key);
		}
		return null;
	}

	public void putCachedItems(RowId key, RowItem rowItem) {
		Cache<RowId, RowItem> cachedItems = cacheManager.getCache(GenericConstants.CACHED_ITEMS_KEY, RowId.class,
				RowItem.class);
		cachedItems.put(key, rowItem);
	}

	public void removeCachedItems(RowId key) {
		Cache<RowId, RowItem> cachedItems = cacheManager.getCache(GenericConstants.CACHED_ITEMS_KEY, RowId.class,
				RowItem.class);
		if (cachedItems.containsKey(key)) {
			cachedItems.remove(key);
		}
	}

	public void removeAllCachedItems() {
		Cache<RowId, RowItem> cachedItems = cacheManager.getCache(GenericConstants.CACHED_ITEMS_KEY, RowId.class,
				RowItem.class);
		cachedItems.clear();
	}

	public <D> void putAllSubTypeOfClass(Class<D> subTypeOf, List<Class<? extends D>> list) {
		Cache<Class, List> cachedItems = cacheManager.getCache(GenericConstants.CACHED_SUBTYPE_OF_CLASS_KEY,
				Class.class, List.class);
		cachedItems.put(subTypeOf, list);
	}

	public <D> boolean containsAllSubTypeOfClassKey(Class<D> subTypeOf) {
		Cache<Class, List> cacheResults = cacheManager.getCache(GenericConstants.CACHED_SUBTYPE_OF_CLASS_KEY,
				Class.class, List.class);
		if (cacheResults != null) {
			return cacheResults.containsKey(subTypeOf);
		}
		return false;
	}

	public List<Class<? extends D>> getAllSubTypeOfClass(Class<D> subTypeOf) {
		Cache<Class, List> cachedItems = cacheManager.getCache(GenericConstants.CACHED_SUBTYPE_OF_CLASS_KEY,
				Class.class, List.class);
		if (cachedItems.containsKey(subTypeOf)) {
			return cachedItems.get(subTypeOf);
		}
		return null;
	}
}
