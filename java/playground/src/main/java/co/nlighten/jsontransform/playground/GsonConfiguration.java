package co.nlighten.jsontransform.playground;

import com.google.gson.ToNumberPolicy;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class GsonConfiguration {

    private static final String dateTime = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @Bean
    public GsonBuilderCustomizer typeAdapterRegistration() {
        return builder -> {
            builder
                    .setDateFormat(dateTime)
                    .registerTypeAdapter(BigDecimal.class, new BigDecimalTypeAdapter())
                    .setNumberToNumberStrategy(ToNumberPolicy.BIG_DECIMAL)
                    .setObjectToNumberStrategy(ToNumberPolicy.BIG_DECIMAL);
        };
    }
}
