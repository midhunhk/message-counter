<p><strong>Note: </strong> This project is currently on hold due to the updated Google Play Policies preventing apps to read from the SMS Database. <br /><a href="https://github.com/midhunhk/message-counter/wiki/FAQ">Read more on the project's FAQ.</a></p> 
<img alt="message counter" src="https://github.com/midhunhk/message-counter/blob/gh-pages/resources/v4/promo_wyvern.png"/>

[![Build Status](https://travis-ci.org/midhunhk/message-counter.svg?branch=master)](https://travis-ci.org/midhunhk/message-counter) 
[![Release](https://img.shields.io/github/release/midhunhk/message-counter.svg)](https://github.com/midhunhk/message-counter/releases) 
[![GitHub commits](https://img.shields.io/github/commits-since/midhunhk/message-counter/v4.0.2.svg)](https://github.com/midhunhk/message-counter) 
[![Issues](https://img.shields.io/github/issues/midhunhk/message-counter.svg)](https://github.com/midhunhk/message-counter/issues) 
[![](https://img.shields.io/badge/codename-xingtian-FF9800.svg)](https://github.com/midhunhk/message-counter/wiki/Codenames)

[Project Wiki](https://github.com/midhunhk/message-counter/wiki) | 
[Github Page](http://midhunhk.github.io/message-counter) | 
[Contribute](https://github.com/midhunhk/message-counter/wiki/Development#contribute) | 
[Milestones](https://github.com/midhunhk/message-counter/milestones) 

Message Counter is a modern Android app that counts the number of SMSes sent in a cycle. 
It is now entirely rewritten in Kotlin and makes use of Android Architecture Components and Kotlin Extensions.

## Features
- Shows number of SMS sent per each cycle
- Notifies when SMS limit per cycle is reached
- Ignore SMS sent to configurable numbers
- Top message senders
- Material Design
- Widgets!
- No Advertisements

## Screenshots
<img alt="Message Counter" src="https://github.com/midhunhk/message-counter/blob/gh-pages/resources/v4/screenshots/en/en-01.png" width="250"/> <img alt="Ignore Numbers" src="https://github.com/midhunhk/message-counter/blob/gh-pages/resources/v4/screenshots/en/en-02.png" width="250"/> <img alt="Message Statistics" src="https://github.com/midhunhk/message-counter/blob/gh-pages/resources/v4/screenshots/en/en-03.png" width="250"/> 

## Google Play Compliance
In order to comply with the latest Google Play policies surrounding the use of certain permissions, 
the core functionality of the app cannot be published on Google Play. [See more details](https://github.com/midhunhk/message-counter/wiki/Permissions#removal-from-google-play)   
To make the app available to all users, it needs to be installed from an [alternate source](https://github.com/midhunhk/message-counter/wiki/Alternate-Sources).  
Note that restriction applies to all apps on Google Play Store. 
 
### Source Code
Clone this repo and import the project as an existing Android project in Android Studio.

```
git clone https://github.com/midhunhk/message-counter.git  

Open the project in Android Studio  
```
 
## Dependencies
 - kotlin-extensions
 - [lib-ae-apps](https://github.com/midhunhk/ae-apps-library)
 - [android-support-library](https://developer.android.com/topic/libraries/support-library/)
 - [android-architecture-components](https://developer.android.com/topic/libraries/architecture/)
 - [anko library](https://github.com/Kotlin/anko)

## License
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
  
 http://www.apache.org/licenses/LICENSE-2.0
  
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
