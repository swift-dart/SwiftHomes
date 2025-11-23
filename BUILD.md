# Building HomePlugin

## Option 1: Using Maven (Recommended)

### Install Maven
1. Download Maven from https://maven.apache.org/download.cgi
2. Extract to a folder (e.g., `C:\Program Files\Maven`)
3. Add Maven's `bin` folder to your PATH environment variable
4. Open a new terminal and verify with `mvn -version`

### Build the Plugin
```bash
cd HomePlugin
mvn clean package
```

The JAR will be in `target/HomePlugin-1.0.0.jar`

## Option 2: Using IntelliJ IDEA

1. Open IntelliJ IDEA
2. File -> Open -> Select the `HomePlugin` folder
3. Wait for Maven to download dependencies
4. Right-click `pom.xml` -> Maven -> Reload Project
5. Open Maven sidebar (View -> Tool Windows -> Maven)
6. Expand HomePlugin -> Lifecycle
7. Double-click `package`
8. The JAR will be in `target/HomePlugin-1.0.0.jar`

## Option 3: Using Eclipse

1. Open Eclipse
2. File -> Import -> Maven -> Existing Maven Projects
3. Browse to `HomePlugin` folder and click Finish
4. Right-click the project -> Run As -> Maven Build
5. In Goals, type `clean package` and click Run
6. The JAR will be in `target/HomePlugin-1.0.0.jar`

## Installing on Your Server

1. Copy `target/HomePlugin-1.0.0.jar` to your server's `plugins` folder
2. Restart your server or use `/reload confirm`
3. The plugin will create a config file at `plugins/HomePlugin/config.yml`
4. Configure as needed and restart if you change settings

## Testing

1. Start your server
2. Join the server
3. Run `/sethome myhouse` - a GUI should open
4. Click the green checkmark to confirm
5. Move somewhere else
6. Run `/home` - a GUI should open showing your home
7. Click on your home to teleport
