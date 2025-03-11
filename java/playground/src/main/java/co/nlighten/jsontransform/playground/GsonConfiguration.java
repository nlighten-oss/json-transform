package co.nlighten.jsontransform.playground;

import co.nlighten.jsontransform.adapters.gson.GsonHelpers;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class GsonConfiguration {

    public static void setupBuilder(GsonBuilder builder) {
        builder
                .setDateFormat(GsonHelpers.ISO_DATETIME_FORMAT)
                .serializeNulls()
                .registerTypeAdapter(BigDecimal.class, new BigDecimalTypeAdapter())
                .setNumberToNumberStrategy(ToNumberPolicy.BIG_DECIMAL)
                .setObjectToNumberStrategy(ToNumberPolicy.BIG_DECIMAL);
    }

    @Bean
    public GsonBuilderCustomizer typeAdapterRegistration() {
        return GsonConfiguration::setupBuilder;
    }
}
