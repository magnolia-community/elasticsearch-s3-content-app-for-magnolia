import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import com.whitelabel.app.elasticsearch.ElasticSearchConnection;
import com.whitelabel.app.event.AddItem;
import com.whitelabel.app.generic.connector.FactoryContainer;
import com.whitelabel.app.generic.connector.GenericContainer;
import com.whitelabel.app.generic.connector.GenericContentConnector;
import com.whitelabel.app.generic.custom.entity.CustomIndexElasticSearch;
import com.whitelabel.app.generic.custom.entity.Log;
import com.whitelabel.app.generic.others.GenericException;
import com.whitelabel.app.generic.search.GenericParamsBuilder;
import com.whitelabel.app.generic.search.Params;
import com.whitelabel.app.generic.ui.table.CustomResultSet;
import com.whitelabel.app.generic.utils.FactoryConverter;

class CommonUseCaseConnectionESTest {
	private FactoryContainer factory;
	private FactoryConverter factoryConverter;
	private GenericContainer container;
	private Class<CustomIndexElasticSearch> customIndex;
	private Class<Log> logClass;
	private ElasticSearchConnection connection;
	private Logger log;
	private Params searchParams;
	private CustomResultSet results;
	private GenericContentConnector elasticSearchContainer;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		customIndex = CustomIndexElasticSearch.class;
		logClass = Log.class;
		factoryConverter = new FactoryConverter(null);
		ElasticSearchConnection params = new ElasticSearchConnection();
		factory = new FactoryContainer();
		container = new GenericContainer(logClass, factory, factoryConverter);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void add() throws GenericException {
		Params addParams = GenericParamsBuilder.createAdd(Log.class, null)
				.addField("id", String.valueOf(Math.random() * 1000)).addField("level", "13").get();

		AddItem<Log> addItem = new AddItem<Log>(new Log(), Log.class);

		container.addItem(addItem.getObj());
		search();
	}

	void search() throws GenericException {

		searchParams = GenericParamsBuilder.createSearch(null).size(100).get();
		results = null;

//		ElasticSearchContainer elasticSearchContainer = createConnection.getElasticSearchContainer("", logClass);
		container = new GenericContainer(logClass, factory, factoryConverter);
		try {
//			results = container.getDelegate().get().getResults(searchParams);
			results.getResults().stream().forEach(obj -> {
				log.error(((Log) ((co.elastic.clients.elasticsearch.core.search.Hit) obj).source()).getId());
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
