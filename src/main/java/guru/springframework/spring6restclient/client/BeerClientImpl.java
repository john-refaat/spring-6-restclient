package guru.springframework.spring6restclient.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restclient.model.BeerDTO;
import guru.springframework.spring6restclient.model.BeerDTOPageImpl;
import guru.springframework.spring6restclient.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.*;

/**
 * Created by jt, Spring Framework Guru.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient {

    public static final String GET_BEER_PATH = "/api/v1/beer";
    public static final String GET_BEER_BY_ID_PATH = "/api/v1/beer/{beerId}";

    private final RestClient.Builder restClientBuilder;

    private final ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    @Override
    public Page<BeerDTO> listBeers() {
        RestClient restClient = restClientBuilder.build();
        BeerDTOPageImpl<BeerDTO> beerDTOPage = restClient.get()
                .uri(uriBuilder -> uriBuilder.path(GET_BEER_PATH).build())
                .retrieve().body(BeerDTOPageImpl.class);

        log.info("List Response: {}", beerDTOPage);
        return beerDTOPage;
    }

    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {
        RestClient restClient = restClientBuilder.build();
        BeerDTOPageImpl beerDTOPage = restClient.get()
                .uri(uriBuilder -> {
            UriBuilder builder = uriBuilder.path(GET_BEER_PATH);
            if (StringUtils.hasText(beerName))
                builder.queryParam("beerName", beerName);
            if (beerStyle != null)
                builder.queryParam("beerStyle", beerStyle);
            if (showInventory != null && showInventory)
                builder.queryParam("showInventory", showInventory);
            if (pageNumber != null)
                builder.queryParam("page", pageNumber);
            if (pageSize != null)
                builder.queryParam("size", pageSize);
            return builder.build();
        }).retrieve().body(BeerDTOPageImpl.class);
        log.info("Page Response: {}", beerDTOPage);
        return beerDTOPage;
    }

    @Override
    public BeerDTO getBeerById(UUID beerId) {
        RestClient restClient = restClientBuilder.build();
        ResponseEntity<BeerDTO> entity = restClient.get()
                .uri(uriBuilder -> uriBuilder.path(GET_BEER_BY_ID_PATH).build(beerId))
                .retrieve().toEntity(BeerDTO.class);
        log.info("Get Response: {}", entity);
        return entity.getBody();
    }

    @Override
    public BeerDTO createBeer(BeerDTO newDto) {
        RestClient restClient = restClientBuilder.build();
        ResponseEntity<Void> bodilessEntity = restClient.post().uri(uriBuilder -> uriBuilder.path(GET_BEER_PATH).build())
                .body(newDto)
                .retrieve().toBodilessEntity();
        log.info("Create Response - Bodiless: {}", bodilessEntity);
        URI location = bodilessEntity.getHeaders().getLocation();

        if(location==null)
            throw new RuntimeException("Location not found in the response");

        return restClient.get().uri(location).retrieve().body(BeerDTO.class);
    }

    @Override
    public BeerDTO updateBeer(BeerDTO beerDto) {
        RestClient restClient = restClientBuilder.build();
        ResponseEntity<Void> entity = restClient.put()
                .uri(uriBuilder -> uriBuilder.path(GET_BEER_BY_ID_PATH).build(beerDto.getId()))
                .body(beerDto)
                .retrieve().toBodilessEntity();
        log.info("Update Response: {}", entity);
        BeerDTO updated = restClient.get().uri(uriBuilder -> uriBuilder.path(GET_BEER_BY_ID_PATH).build(beerDto.getId()))
                .retrieve().body(BeerDTO.class);
        log.info("Updated Beer: {}", updated);
        return updated;
    }

    @Override
    public void deleteBeer(UUID beerId) {
        RestClient restClient = restClientBuilder.build();
        ResponseEntity<Void> bodilessEntity = restClient.delete().uri(uriBuilder -> uriBuilder.path(GET_BEER_BY_ID_PATH).build(beerId))
                .retrieve().toBodilessEntity();
        log.info("Delete Response - Bodiless: {}", bodilessEntity);
    }
}
