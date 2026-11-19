
package com.example.mesaclick.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class ImagenConfig implements WebMvcConfigurer {

    @Value("${mesaclick.upload-dir:uploads/productos}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry
    ) {
        String ubicacion = Path.of(uploadDir)
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();

        registry.addResourceHandler("/imagenes/productos/**")
                .addResourceLocations(ubicacion);
    }
}
