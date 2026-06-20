package edu.kiu.uno.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import edu.kiu.uno.CoreTest;
import edu.kiu.uno.UnoCli;
import edu.kiu.uno.service.StatisticsQueryService.QueryType;

public class StatisticsQueryServiceTest extends CoreTest {

	@Autowired
	private StatisticsQueryService statisticsQueryService;
	@Autowired
	private UnoCli unoCli;

	@ParameterizedTest
	@EnumSource(QueryType.class)
	void testQueryStatistics(QueryType queryType) {
		// Given - game is played and statistics are available

		// When - query is executed
		var result = statisticsQueryService.query(queryType.name());

		// Then - result is not null or empty
		assertThat(result).isNotBlank();
	}


}
