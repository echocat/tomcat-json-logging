# tomcat-json-logging

Extension to Tomcat logging that will format the contents as JSON to be better compatible with logging environments for microservices.

## TOC

1. [Example](#example)
2. [Installation](#installation)
3. [Customization](#customization)
4. [FAQ](#faq)
5. [Contributing](#contributing)
6. [License](#license)

## Example

If everything is configured correctly you will see output like this on our console:
```json
..
{"when":"2018-11-07T14:40:58.540+0100","level":"INFO","source":"org.apache.coyote.AbstractProtocol.start(AbstractProtocol.java:488)","logger":"org.apache.coyote.http11.Http11AprProtocol","thread":"main","processId":22640,"message":"Starting ProtocolHandler [\"http-apr-8080\"]"}
{"when":"2018-11-07T14:40:58.555+0100","level":"INFO","source":"org.apache.coyote.AbstractProtocol.start(AbstractProtocol.java:488)","logger":"org.apache.coyote.ajp.AjpAprProtocol","thread":"main","processId":22640,"message":"Starting ProtocolHandler [\"ajp-apr-8009\"]"}
{"when":"2018-11-07T14:40:58.560+0100","level":"INFO","source":"org.apache.catalina.startup.Catalina.start(Catalina.java:654)","logger":"org.apache.catalina.startup.Catalina","thread":"main","processId":22640,"message":"Server startup in 859 ms"}
..
```

## Installation

### 1. Download

Go to [Release page](https://github.com/echocat/tomcat-json-logging/releases/latest) and download the matching version of `tomcat-juli.jar` for your Tomcat version. If you have Tomcat `8.5.32` running download `tomcat-juli-8.5.32.jar` and so on...

Replace `bin/tomcat-juli.jar` inside your Tomcat distribution directory with this file.

> No worries! This files is a combined version of the original Tomcat version and the new functionality.

### 2. Configure

Just replace the content of `conf/logging.properties` in your Tomcat distribution directory with the following content:
```properties
handlers = org.echocat.tjl.Handler
```

This will log everything directly to the console in JSON format (by default).

## Customization

### Using system properties or environment variables

You can either specify a system property or environment variable to change the behavior of the logging of the Tomcat. This values always override every settings in `conf/logging.properties`.

##### Available properties

| System property | Environment property | Description |
|---|---|---|
| `log.format` | `LOG_FORMAT` | Forces a different formatter. See [Available formats](#available-formats). |
| `log.level` | `LOG_LEVEL` | Forces a different global log level. Pleas refer [`java.util.logging.Level`](https://docs.oracle.com/javase/8/docs/api/java/util/logging/Level.html) for different levels. For example `FINE` for more output while local development. |

##### Available formats

| Value | Description |
|---|---|
| `json` | Will use `org.echocat.tjl.JsonFormatter` - see [Available formatter](#available-formatter) for more information. |
| `text` | Will use `org.echocat.tjl.TextFormatter` - see [Available formatter](#available-formatter) for more information. |

### Using `conf/logging.properties`

##### Available properties

| Name | Description | Default |
|------|-------------|---------|
| `org.echocat.tjl.Handler.level` | Minimum level of log entries to be logged/visible. Pleas refer [`java.util.logging.Level`](https://docs.oracle.com/javase/8/docs/api/java/util/logging/Level.html) for more information.  | `ALL` |
| `org.echocat.tjl.Handler.formatter` | Formatter to format the message in the console. | `org.echocat.tjl.JsonFormatter` |

##### Available formatter

| Name | Description |
|------|-------------|
| `org.echocat.tjl.JsonFormatter` | Formats the whole output as JSON. One entry per line. |
| `org.echocat.tjl.TextFormatter` | Formats every entry per line in a simple way - better for local debugging. |

## FAQ

#### How to activate debug output temporary?

```bash
LOG_LEVEL=debug bin/catalina.sh run
```

#### How to make output temporarily better readable?

```bash
LOG_FORMAT=text bin/catalina.sh run
```

## Contributing

tomcat-json-logging is an open source project by [echocat](https://echocat.org).
So if you want to make this project even better, you can contribute to this project on [Github](https://github.com/echocat/tomcat-json-logging)
by [fork us](https://github.com/echocat/tomcat-json-logging/fork).

If you commit code to this project, you have to accept that this code will be released under the [license](#license) of this project.

## License

See the [LICENSE](LICENSE) file.