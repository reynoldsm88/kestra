package io.kestra.webserver.controllers;

import io.kestra.core.repositories.ExecutionRepositoryInterface;
import io.kestra.core.services.InstanceService;
import io.kestra.core.utils.VersionProvider;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.inject.Inject;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MiscController {
    @Inject
    VersionProvider versionProvider;

    @Inject
    ExecutionRepositoryInterface executionRepository;

    @Inject
    InstanceService instanceService;

    @Get("/ping")
    @Hidden
    public HttpResponse<?> ping() {
        return HttpResponse.ok("pong");
    }

    @Get("/api/v1/configs")
    @ExecuteOn(TaskExecutors.IO)
    @Operation(tags = {"Misc"}, summary = "Get current configurations")
    public Configuration configuration() {
        return Configuration
            .builder()
            .uuid(instanceService.fetch())
            .version(versionProvider.getVersion())
            .isTaskRunEnabled(executionRepository.isTaskRunEnabled())
            .build();
    }

    @Value
    @Builder
    public static class Configuration {
        String uuid;
        String version;
        Boolean isTaskRunEnabled;
    }
}
