[ ![Download](https://api.bintray.com/packages/beeline09/UniDevIdLib/ru.unidevid.lib/images/download.svg) ](https://bintray.com/beeline09/UniDevIdLib/ru.unidevid.lib/)
# android-uniDevId
Android Iniversal Device ID Library

- Use OpenUDID_manager for old API and MediaDrm for new API
- If both options return an empty string, then a random UUID is generated and saved in Preferences until the application is reinstalled

### Installation

- First add this repo to your project:```maven { url "https://dl.bintray.com/beeline09/UniDevIdLib" }```
- Second add this dependency to your module build.gradle: ```implementation 'ru.unidevid.lib:UniDevIdLib:1.0.0'```

### Usage
Before get Device ID we need to init lib by command: ```UdidManager.init(this)```.
It is highly recommended to do this in `onCreate` method of Application class

Для получения Device UUID  в любом месте приложения надо вызвать: `UdidManager.getUUID()`
