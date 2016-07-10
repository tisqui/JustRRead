# JustRRead
Posts reader for Reddit.

### To build the project
In file gradle.properties you should add the following:

**MyAppId="unique_id_of_the_app_on_reddit"**

To get the id you should register on reddit.com and go to [here](https://www.reddit.com/prefs/apps/), where the new app can be created. 
Reddit provides the unique id for every app to use API.

**MyUserName="your_reddit_username"**

The account you registered on reddit and used for getting app id.

**MyTestDevice="your_test_device_id"**

As the app shows test google ads, the device id you use for testing should be entered. How to get the id of your test device you can learn [here] (https://firebase.google.com/docs/admob/android/targeting#test_ads)

###To make the google analytics work

Add yout GA tracking number in /res/xml/track_app.xml in

    <string name="ga_trackingId"></string>

To get the tracking id you should register at https://analytics.google.com/ 
