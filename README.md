# android-uniDevId
Android Iniversal Device ID Library

- Use OpenUDID_manager for old API and MediaDrm for new API
- If both options return an empty string, then a random UUID is generated and saved in Preferences until the application is reinstalled

### Installation

- First add this repo to your project:```mavenCentral()```
- Second add this dependency to your module build.gradle: ```implementation 'io.github.beeline09.android-uniDevId:uniDevId:1.0.3'```

### Usage
Before get Device ID we need to init lib by command: ```UdidManager.init(this)```.
It is highly recommended to do this in `onCreate` method of Application class

For get Device UUID in any place of your app, you need to call: `UdidManager.getUUID()`
