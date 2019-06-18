package imdb1000;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieInfoController {

    @RequestMapping("/movies")
    public MovieInfo greeting(@RequestParam(value="names") String[] names) {
        return new MovieInfo(names);
    }
}
