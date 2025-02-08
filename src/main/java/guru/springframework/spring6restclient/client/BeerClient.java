package guru.springframework.spring6restclient.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import guru.springframework.spring6restclient.model.BeerDTO;
import guru.springframework.spring6restclient.model.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.UUID;

/**
 * Created by jt, Spring Framework Guru.
 */
public interface BeerClient {
    Page<BeerDTO> listBeers() throws JsonProcessingException;

    Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber,
                            Integer pageSize);

    BeerDTO getBeerById(UUID beerId);

    BeerDTO createBeer(BeerDTO newDto);

    BeerDTO updateBeer(BeerDTO beerDto);

    void deleteBeer(UUID beerId);
}
