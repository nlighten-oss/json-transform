package co.nlighten.jsontransform.playground;

import co.nlighten.jsontransform.adapters.gson.GsonJsonAdapter;
import com.google.gson.ToNumberPolicy;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class GsonConfiguration {

    @Bean
    public GsonBuilderCustomizer typeAdapterRegistration() {
        return builder -> {
            builder
                .setDateFormat(GsonJsonAdapter.ISO_DATETIME_FORMAT)
                .serializeNulls()
                .registerTypeAdapter(BigDecimal.class, new BigDecimalTypeAdapter())
                .setNumberToNumberStrategy(ToNumberPolicy.BIG_DECIMAL)
                .setObjectToNumberStrategy(ToNumberPolicy.BIG_DECIMAL);
        };
    }
}
