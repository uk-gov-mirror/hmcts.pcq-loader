@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  pcq-consolidation-service startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and PCQ_CONSOLIDATION_SERVICE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\pcq-consolidation-service-0.0.1.jar;%APP_HOME%\lib\spring-boot-starter-web-services-2.3.0.RELEASE.jar;%APP_HOME%\lib\properties-volume-spring-boot-starter-0.0.4.jar;%APP_HOME%\lib\spring-boot-starter-web-2.3.0.RELEASE.jar;%APP_HOME%\lib\service-auth-provider-client-3.1.1.jar;%APP_HOME%\lib\idam-client-1.3.2.jar;%APP_HOME%\lib\core-case-data-store-client-4.6.4.jar;%APP_HOME%\lib\spring-boot-starter-actuator-2.3.0.RELEASE.jar;%APP_HOME%\lib\spring-cloud-starter-netflix-hystrix-2.2.1.RELEASE.jar;%APP_HOME%\lib\spring-cloud-netflix-hystrix-2.2.1.RELEASE.jar;%APP_HOME%\lib\spring-cloud-starter-openfeign-2.2.1.RELEASE.jar;%APP_HOME%\lib\spring-cloud-openfeign-core-2.2.1.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-aop-2.3.0.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-json-2.3.0.RELEASE.jar;%APP_HOME%\lib\spring-boot-configuration-processor-2.3.0.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-security-2.3.0.RELEASE.jar;%APP_HOME%\lib\lombok-1.18.12.jar;%APP_HOME%\lib\json-schema-validator-1.0.31.jar;%APP_HOME%\lib\feign-form-spring-3.8.0.jar;%APP_HOME%\lib\feign-form-3.8.0.jar;%APP_HOME%\lib\springfox-swagger2-2.9.2.jar;%APP_HOME%\lib\logging-5.1.1.jar;%APP_HOME%\lib\logging-appinsights-5.1.1.jar;%APP_HOME%\lib\spring-boot-starter-validation-2.3.0.RELEASE.jar;%APP_HOME%\lib\spring-cloud-starter-netflix-archaius-2.2.1.RELEASE.jar;%APP_HOME%\lib\spring-cloud-starter-2.2.1.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-2.3.0.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-tomcat-2.3.0.RELEASE.jar;%APP_HOME%\lib\spring-ws-core-3.0.9.RELEASE.jar;%APP_HOME%\lib\spring-webmvc-5.2.6.RELEASE.jar;%APP_HOME%\lib\spring-security-web-5.3.2.RELEASE.jar;%APP_HOME%\lib\spring-web-5.2.6.RELEASE.jar;%APP_HOME%\lib\saaj-impl-1.5.2.jar;%APP_HOME%\lib\jakarta.xml.ws-api-2.3.3.jar;%APP_HOME%\lib\spring-oxm-5.2.6.RELEASE.jar;%APP_HOME%\lib\spring-boot-actuator-autoconfigure-2.3.0.RELEASE.jar;%APP_HOME%\lib\micrometer-core-1.5.1.jar;%APP_HOME%\lib\spring-security-config-5.3.2.RELEASE.jar;%APP_HOME%\lib\springfox-swagger-common-2.9.2.jar;%APP_HOME%\lib\springfox-spring-web-2.9.2.jar;%APP_HOME%\lib\springfox-schema-2.9.2.jar;%APP_HOME%\lib\springfox-spi-2.9.2.jar;%APP_HOME%\lib\springfox-core-2.9.2.jar;%APP_HOME%\lib\spring-plugin-metadata-1.2.0.RELEASE.jar;%APP_HOME%\lib\spring-plugin-core-1.2.0.RELEASE.jar;%APP_HOME%\lib\spring-boot-autoconfigure-2.3.0.RELEASE.jar;%APP_HOME%\lib\spring-boot-actuator-2.3.0.RELEASE.jar;%APP_HOME%\lib\spring-boot-2.3.0.RELEASE.jar;%APP_HOME%\lib\spring-xml-3.0.9.RELEASE.jar;%APP_HOME%\lib\spring-security-core-5.3.2.RELEASE.jar;%APP_HOME%\lib\spring-context-5.2.6.RELEASE.jar;%APP_HOME%\lib\spring-aop-5.2.6.RELEASE.jar;%APP_HOME%\lib\hystrix-javanica-1.5.18.jar;%APP_HOME%\lib\aspectjweaver-1.9.5.jar;%APP_HOME%\lib\jackson-datatype-jdk8-2.11.0.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.11.0.jar;%APP_HOME%\lib\jackson-module-parameter-names-2.11.0.jar;%APP_HOME%\lib\hystrix-metrics-event-stream-1.5.18.jar;%APP_HOME%\lib\hystrix-serialization-1.5.18.jar;%APP_HOME%\lib\logstash-logback-encoder-6.1.jar;%APP_HOME%\lib\java-jwt-3.4.0.jar;%APP_HOME%\lib\feign-jackson-10.9.jar;%APP_HOME%\lib\feign-hystrix-10.4.0.jar;%APP_HOME%\lib\hystrix-core-1.5.18.jar;%APP_HOME%\lib\archaius-core-0.7.6.jar;%APP_HOME%\lib\jackson-module-afterburner-2.11.0.jar;%APP_HOME%\lib\jackson-databind-2.11.0.jar;%APP_HOME%\lib\spring-cloud-netflix-ribbon-2.2.1.RELEASE.jar;%APP_HOME%\lib\rxjava-reactive-streams-1.2.1.jar;%APP_HOME%\lib\spring-cloud-commons-2.2.1.RELEASE.jar;%APP_HOME%\lib\feign-slf4j-10.4.0.jar;%APP_HOME%\lib\feign-httpclient-10.9.jar;%APP_HOME%\lib\feign-core-10.9.jar;%APP_HOME%\lib\swagger-models-1.5.20.jar;%APP_HOME%\lib\spring-boot-starter-logging-2.3.0.RELEASE.jar;%APP_HOME%\lib\jul-to-slf4j-1.7.30.jar;%APP_HOME%\lib\applicationinsights-logging-logback-2.5.1.jar;%APP_HOME%\lib\logback-classic-1.2.3.jar;%APP_HOME%\lib\log4j-to-slf4j-2.13.2.jar;%APP_HOME%\lib\slf4j-api-1.7.30.jar;%APP_HOME%\lib\commons-lang3-3.10.jar;%APP_HOME%\lib\commons-fileupload-1.4.jar;%APP_HOME%\lib\swagger-annotations-1.5.20.jar;%APP_HOME%\lib\guava-28.2-jre.jar;%APP_HOME%\lib\hibernate-validator-6.1.5.Final.jar;%APP_HOME%\lib\classmate-1.5.1.jar;%APP_HOME%\lib\mapstruct-1.2.0.Final.jar;%APP_HOME%\lib\applicationinsights-spring-boot-starter-2.5.1.jar;%APP_HOME%\lib\googleauth-1.1.5.jar;%APP_HOME%\lib\javax.servlet-api-4.0.1.jar;%APP_HOME%\lib\jakarta.el-3.0.3.jar;%APP_HOME%\lib\jakarta.annotation-api-1.3.5.jar;%APP_HOME%\lib\spring-beans-5.2.6.RELEASE.jar;%APP_HOME%\lib\spring-expression-5.2.6.RELEASE.jar;%APP_HOME%\lib\spring-core-5.2.6.RELEASE.jar;%APP_HOME%\lib\snakeyaml-1.26.jar;%APP_HOME%\lib\tomcat-embed-websocket-9.0.35.jar;%APP_HOME%\lib\tomcat-embed-core-9.0.35.jar;%APP_HOME%\lib\jakarta.xml.soap-api-1.4.2.jar;%APP_HOME%\lib\stax-ex-1.8.3.jar;%APP_HOME%\lib\jakarta.activation-1.2.2.jar;%APP_HOME%\lib\jakarta.xml.bind-api-2.3.3.jar;%APP_HOME%\lib\jakarta.jws-api-2.1.0.jar;%APP_HOME%\lib\HdrHistogram-2.1.12.jar;%APP_HOME%\lib\LatencyUtils-2.0.3.jar;%APP_HOME%\lib\jackson-annotations-2.11.0.jar;%APP_HOME%\lib\jackson-core-2.11.0.jar;%APP_HOME%\lib\spring-cloud-context-2.2.1.RELEASE.jar;%APP_HOME%\lib\spring-security-rsa-1.0.9.RELEASE.jar;%APP_HOME%\lib\spring-cloud-netflix-archaius-2.2.1.RELEASE.jar;%APP_HOME%\lib\commons-configuration-1.8.jar;%APP_HOME%\lib\rxjava-1.3.8.jar;%APP_HOME%\lib\asm-5.0.4.jar;%APP_HOME%\lib\reactive-streams-1.0.3.jar;%APP_HOME%\lib\spring-security-crypto-5.3.2.RELEASE.jar;%APP_HOME%\lib\commons-io-2.2.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\checker-qual-2.10.0.jar;%APP_HOME%\lib\error_prone_annotations-2.3.4.jar;%APP_HOME%\lib\j2objc-annotations-1.3.jar;%APP_HOME%\lib\logback-core-1.2.3.jar;%APP_HOME%\lib\applicationinsights-core-2.5.1.jar;%APP_HOME%\lib\applicationinsights-web-2.5.1.jar;%APP_HOME%\lib\httpclient-4.5.12.jar;%APP_HOME%\lib\commons-codec-1.14.jar;%APP_HOME%\lib\jakarta.validation-api-2.0.2.jar;%APP_HOME%\lib\jboss-logging-3.4.1.Final.jar;%APP_HOME%\lib\spring-jcl-5.2.6.RELEASE.jar;%APP_HOME%\lib\bcpkix-jdk15on-1.64.jar;%APP_HOME%\lib\commons-lang-2.6.jar;%APP_HOME%\lib\byte-buddy-1.10.10.jar;%APP_HOME%\lib\httpcore-4.4.13.jar;%APP_HOME%\lib\log4j-api-2.13.2.jar;%APP_HOME%\lib\bcprov-jdk15on-1.64.jar

@rem Execute pcq-consolidation-service
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %PCQ_CONSOLIDATION_SERVICE_OPTS%  -classpath "%CLASSPATH%" uk.gov.hmcts.reform.pcqconsolidationservice.ConsolidationApplication %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable PCQ_CONSOLIDATION_SERVICE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%PCQ_CONSOLIDATION_SERVICE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
