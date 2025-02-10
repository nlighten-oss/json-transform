package co.nlighten.jsontransform.playground;

import co.nlighten.jsontransform.JsonTransformerConfiguration;
import co.nlighten.jsontransform.adapters.gson.GsonJsonTransformerConfiguration;
import co.nlighten.jsontransform.adapters.jackson.JacksonJsonTransformerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlaygroundApplication {

	public static void main(String[] args) {
		JsonTransformerConfiguration.set(new GsonJsonTransformerConfiguration());
		//JsonTransformerConfiguration.set(new JacksonJsonTransformerConfiguration());
        SpringApplication.run(PlaygroundApplication.class, args);
	}
}
