/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.workspace.infrastructure.docker.environment.convert;

import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.che.api.core.ValidationException;
import org.eclipse.che.api.workspace.server.spi.environment.InternalEnvironment;
import org.eclipse.che.workspace.infrastructure.docker.environment.compose.ComposeEnvironment;
import org.eclipse.che.workspace.infrastructure.docker.environment.compose.model.ComposeService;
import org.eclipse.che.workspace.infrastructure.docker.model.DockerBuildContext;
import org.eclipse.che.workspace.infrastructure.docker.model.DockerContainerConfig;
import org.eclipse.che.workspace.infrastructure.docker.model.DockerEnvironment;

/**
 * Converts {@link ComposeEnvironment} to {@link DockerEnvironment}.
 *
 * @author Alexander Garagatyi
 * @author Alexander Andrienko
 */
public class ComposeEnvironmentConverter implements DockerEnvironmentConverter {

  @Override
  public DockerEnvironment convert(InternalEnvironment environment) throws ValidationException {
    if (!(environment instanceof ComposeEnvironment)) {
      throw new ValidationException("The specified environment is not compose environment");
    }
    ComposeEnvironment composeEnv = (ComposeEnvironment) environment;

    LinkedHashMap<String, DockerContainerConfig> containers =
        Maps.newLinkedHashMapWithExpectedSize(composeEnv.getServices().size());
    for (Map.Entry<String, ComposeService> composeServiceEntry :
        composeEnv.getServices().entrySet()) {
      ComposeService service = composeServiceEntry.getValue();

      DockerContainerConfig cheContainer =
          new DockerContainerConfig()
              .setCommand(service.getCommand())
              .setContainerName(service.getContainerName())
              .setDependsOn(service.getDependsOn())
              .setEntrypoint(service.getEntrypoint())
              .setEnvironment(service.getEnvironment())
              .setExpose(service.getExpose())
              .setImage(service.getImage())
              .setLabels(service.getLabels())
              .setLinks(service.getLinks())
              .setMemLimit(service.getMemLimit())
              .setNetworks(service.getNetworks())
              .setPorts(service.getPorts())
              .setVolumes(service.getVolumes())
              .setVolumesFrom(service.getVolumesFrom());

      if (service.getBuild() != null) {
        cheContainer.setBuild(
            new DockerBuildContext()
                .setContext(service.getBuild().getContext())
                .setDockerfilePath(service.getBuild().getDockerfile())
                .setArgs(service.getBuild().getArgs()));
      }

      containers.put(composeServiceEntry.getKey(), cheContainer);
    }
    return new DockerEnvironment(environment)
        .setContainers(containers)
        .setType(DockerEnvironment.TYPE);
  }
}
