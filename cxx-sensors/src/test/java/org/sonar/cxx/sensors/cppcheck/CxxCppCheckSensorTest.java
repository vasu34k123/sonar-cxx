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
package org.sonar.cxx.sensors.cppcheck;

import org.sonar.cxx.sensors.cppcheck.CxxCppCheckSensor;
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
import org.sonar.cxx.sensors.utils.TestUtils;

public class CxxCppCheckSensorTest {

  private DefaultFileSystem fs;
  private CxxLanguage language;
  private MapSettings settings = new MapSettings();

  @Before
  public void setUp() {
    fs = TestUtils.mockFileSystem();
    language = TestUtils.mockCxxLanguage();
    when(language.getPluginProperty(CxxCppCheckSensor.REPORT_PATH_KEY)).thenReturn("sonar.cxx." + CxxCppCheckSensor.REPORT_PATH_KEY);
    when(language.IsRecoveryEnabled()).thenReturn(Optional.of(Boolean.TRUE));
    }

  @Test
  public void shouldReportCorrectViolations() {
    SensorContextTester context = SensorContextTester.create(fs.baseDir());

    settings.setProperty(language.getPluginProperty(CxxCppCheckSensor.REPORT_PATH_KEY), "cppcheck-reports/cppcheck-result-*.xml");
    context.setSettings(settings);

    CxxCppCheckSensor sensor = new CxxCppCheckSensor(language);
    context.fileSystem().add(TestInputFileBuilder.create("ProjectKey", "sources/utils/code_chunks.cpp")
                             .setLanguage("cpp").initMetadata(new String("asd\nasdas\nasda\n")).build());
    context.fileSystem().add(TestInputFileBuilder.create("ProjectKey", "sources/utils/utils.cpp")
                             .setLanguage("cpp").initMetadata(new String("asd\nasdas\nasda\n")).build());
    sensor.execute(context);
    assertThat(context.allIssues()).hasSize(9);
  }

  @Test
  public void shouldReportProjectLevelViolationsV1() {
    SensorContextTester context = SensorContextTester.create(fs.baseDir());

    settings.setProperty(language.getPluginProperty(CxxCppCheckSensor.REPORT_PATH_KEY), "cppcheck-reports/cppcheck-result-projectlevelviolation-V1.xml");
    context.setSettings(settings);
    
    CxxCppCheckSensor sensor = new CxxCppCheckSensor(language);
    sensor.execute(context);
    assertThat(context.allIssues()).hasSize(3);
  }

  @Test
  public void shouldReportProjectLevelViolationsV2() {
    SensorContextTester context = SensorContextTester.create(fs.baseDir());

    settings.setProperty(language.getPluginProperty(CxxCppCheckSensor.REPORT_PATH_KEY), "cppcheck-reports/cppcheck-result-projectlevelviolation-V2.xml");
    context.setSettings(settings);

    CxxCppCheckSensor sensor = new CxxCppCheckSensor(language);
    sensor.execute(context);
    assertThat(context.allIssues()).hasSize(3);
  }

  @Test
  public void shouldIgnoreAViolationWhenTheResourceCouldntBeFoundV1() {
    SensorContextTester context = SensorContextTester.create(fs.baseDir());

    settings.setProperty(language.getPluginProperty(CxxCppCheckSensor.REPORT_PATH_KEY), "cppcheck-reports/cppcheck-result-SAMPLE-V1.xml");
    context.setSettings(settings);

    CxxCppCheckSensor sensor = new CxxCppCheckSensor(language);
    sensor.execute(context);
    assertThat(context.allIssues()).hasSize(0);
  }

  @Test
  public void shouldIgnoreAViolationWhenTheResourceCouldntBeFoundV2() {
    SensorContextTester context = SensorContextTester.create(fs.baseDir());

    settings.setProperty(language.getPluginProperty(CxxCppCheckSensor.REPORT_PATH_KEY), "cppcheck-reports/cppcheck-result-SAMPLE-V2.xml");
    context.setSettings(settings);

    CxxCppCheckSensor sensor = new CxxCppCheckSensor(language);
    sensor.execute(context);
    assertThat(context.allIssues()).hasSize(0);
  }
  
  @Test(expected=IllegalStateException.class)
  public void shouldThrowExceptionWhenRecoveryIsDisabled() {
    SensorContextTester context = SensorContextTester.create(fs.baseDir());

    when(language.IsRecoveryEnabled()).thenReturn(Optional.of(Boolean.FALSE));
    settings.setProperty(language.getPluginProperty(CxxCppCheckSensor.REPORT_PATH_KEY), "cppcheck-reports/cppcheck-result-empty.xml");
    context.setSettings(settings);

    CxxCppCheckSensor sensor = new CxxCppCheckSensor(language);
    sensor.execute(context);
  }  
}


