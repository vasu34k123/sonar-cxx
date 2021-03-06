/*
 * Sonar C++ Plugin (Community)
 * Copyright (C) 2010-2017 SonarOpenCommunity
 * http://github.com/SonarOpenCommunity/sonar-cxx
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.cxx.sensors.veraxx;

import org.sonar.cxx.sensors.utils.TestUtils;
import static org.assertj.core.api.Assertions.assertThat;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.cxx.CxxLanguage;

public class CxxVeraxxSensorTest {

  private DefaultFileSystem fs;
  private CxxLanguage language;
  private MapSettings settings = new MapSettings();

  @Before
  public void setUp() {
    fs = TestUtils.mockFileSystem();
    language = TestUtils.mockCxxLanguage();
    when(language.getPluginProperty(CxxVeraxxSensor.REPORT_PATH_KEY)).thenReturn("sonar.cxx." + CxxVeraxxSensor.REPORT_PATH_KEY);
    when(language.IsRecoveryEnabled()).thenReturn(Optional.of(Boolean.TRUE));
    }

  @Test
  public void shouldReportCorrectViolations() {
    SensorContextTester context = SensorContextTester.create(fs.baseDir());

    settings.setProperty(language.getPluginProperty(CxxVeraxxSensor.REPORT_PATH_KEY), "vera++-reports/vera++-result-*.xml");
    context.setSettings(settings);
    
    CxxVeraxxSensor sensor = new CxxVeraxxSensor(language);
    
    context.fileSystem().add(TestInputFileBuilder.create("ProjectKey", "sources/application/main.cpp")
                             .setLanguage("cpp").initMetadata("asd\nasdas\nasda\n").build());
    context.fileSystem().add(TestInputFileBuilder.create("ProjectKey", "sources/tests/SAMPLE-test.cpp")
                             .setLanguage("cpp").initMetadata("asd\nasdas\nasda\n").build());
    context.fileSystem().add(TestInputFileBuilder.create("ProjectKey", "sources/tests/SAMPLE-test.h")
                             .setLanguage("cpp").initMetadata("asd\nasdas\nasda\n").build());
    context.fileSystem().add(TestInputFileBuilder.create("ProjectKey", "sources/tests/main.cpp")
                             .setLanguage("cpp").initMetadata("asd\nasdas\nasda\n").build());
    context.fileSystem().add(TestInputFileBuilder.create("ProjectKey", "sources/utils/code_chunks.cpp")
                             .setLanguage("cpp").initMetadata("asd\nasdas\nasda\n").build());
    context.fileSystem().add(TestInputFileBuilder.create("ProjectKey", "sources/utils/utils.cpp")
                             .setLanguage("cpp").initMetadata("asd\nasdas\nasda\n").build());
    sensor.execute(context);
    assertThat(context.allIssues()).hasSize(10);
  }
}

