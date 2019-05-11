## Message Counter Version History

### Version 4.1 (Xingtian)
**MessageCounter 4.1.1 [May 2019]**
 - Update Translations for legacy app
 - Integrate with Firebase Analytics #64

**MessageCounter 4.1.0 [Mar 2019]**
 - 'READ_SMS' permission removed for compliance with Google Play Policies
 - Created a new app with all features for Alternate App Stores #61
 
### Version 4.0 (Wyvern)
 
**MessageCounter 4.0.2 [Oct 2018]**
 - Fix for JobService scheduling issues #55
 - Consume In-App Purchases #57
 - Use new In-App products 
 
**MessageCounter 4.0.1 [Sep 2018]**
 - Prevent multiple indexing at same time
 - Set LaunchMode for MainActivity
 - Handle IllegalStateException when setting Fragment 
 
**MessageCounter 4.0.0 [Sep 2018]**
 - Application rewrite and new architecture #43
 - Improve performance
 - Use Material Theme

### Version 3.3 (Valkyrie)
**MessageCounter 3.3.0 [Oct 2017]**
 - Bottom Navigation View #25
 - Ignore List
 
Version 3.2 (Unicorn)
----------------------
**MessageCounter 3.2.0 [May 2017]**
 - UI Tweaks
 - Performance Enhancements
 - Experimental Feature to count messages sent when service is not running

**MessageCounter 3.2.1 [Aug 2017]**
 - Support for German #15
 - Multi Length message support

Version 3.0 (Talisman)
----------------------
**MessageCounter 3.0.30 [Dec 2014]**
  - [CHANGED]	Redesign Counter fragment 
  - [CHANGED]	Use CardView
  - [CHANGED]	Removed Chart Fragment as it is a bit buggy
  - [CHANGED]	Use Toolbar on applicable screens
  - [CHANGED]	basic material design 
  - [CHANGED]	Message Counter is enabled by default
  
**MessageCounter 3.0.32 [Dec 2014]**
  - [CHANGED]	Removed Non Contact Messages option in settings
  - [FIXED]		Fix for NoSuchMethodError for getMinimumHeight
  
**MessageCounter 3.0.35 [Jan 2015]**
  - [CHANGED]	Use toolbar in SettingsActivity
  
**MessageCounter 3.0.50 [Feb 2015]**
  - [ADDED]		Support for Navigation Drawer
  - [CHANGED]	Bring back Chart Fragment on popular demand
  - [FIXED]		NPE when pressing hardware menu button on LG Devices
  - [CHANGED]	New color scheme for Chart
  - [CHANGED]	More Material Design updates
  - [REMOVED]	Animation for message summary
  
**MessageCounter 3.1.0 [Aug 2015]**
  - [ADDED]		In-App Donations
  - [CHANGED]	About screen reorganization
  - [CHANGED]	Navigation Drawer updates
  - [CHANGED]	UI Updates
  
**MessageCounter 3.1.1 [Feb 2016]**
  - [ADDED]		Messages sent this week
  - [CHANGED]	Navigation Drawer updates
  - [REMOVED]	Menu in toolbar moved to Navigation Drawer
  - [CHANGED]	UI Adjustments

Version 2.3 (Silvermoon)
------------------------
**MessageCounter 2.3.12 [Jul 2014]**
 - [ADDED]		Widget for messages sent
 - [CHANGED]	Settings icon
 - [CHANGED]	Settings menu to actionbar
 - [CHANGED]	UI Tweaks
 - [ADDED]		Widget update broadcast when sending message
 
**MessageCounter 2.3.16 [Jul 2014]**
 - [FIXED]		Minor bug Fixes for Xperia and HTC devices
 - [CHANGED]	Counter fragment is main fragment

**MessageCounter 2.3.20 [Aug 2014]**
 - [FIXED]		Minor bug Fixes as reported
 - [FIXED]		Fixed cycle limit -1 in widget
 
**MessageCounter 2.3.24 [Oct 2014]**
 - [FIXED]		Minor bug fixes as reported

Version 2.2 (Silversun)
-----------------------
**MessageCounter 2.2.50 [Jan 2014]**
 - [CHANGED]	App Icon and color scheme to holo violet
 - [CHANGED]	About screen layout
 - [ADDED]		Sent SMS Counter
 - [CHANGED]	Updated App Icon and color scheme to holo blue
 - [ADDED]		ShareActionProvider in ActionBar
 - [ADDED]		Different ActionBar logo
 - [ADDED]		Toggle count stat page content when enabled or disabled
 - [FIXED]		-1 messages sent
 
**MessageCounter 2.2.67 [Feb 2014]**
 - [FIXED]		Bugs in Sent Message counter
 
**MessageCounter 2.2.74 [Mar 2014]**
 - [ADDED]		Notification on reaching cycle limit
 - [CHANGED]	Sent messages count ui tweaks
 - [CHANGED]	Unobstructed loading screen
 
**MessageCounter 2.2.85 [May 2014]**
 - [CHANGED]	Using Pager Tab Strip
 - [ADDED] 		Previous cycle stats
 - [CHANGED]	Sent Messages screen layout
 - [CHANGED]	Simplified UI
 
**MessageCounter 2.2.90 [Jun 2014]**
 - [FIXED]		Service was not started after app update

Version 2.1 (Reindeer)
------------------
**MessageCounter 2.1.20 [Dec 2013]**
 - [CHANGED]	Supports API 8 and above
 - [CHANGED]	ViewPager implementation to support more devices
 - [CHANGED]	Layout Improvements
 - [ADDED]		Minor animations
 - [CHANGED]	About page moved as a fragment from menu
 - [OPTIMIZED]	Operations
 
**MessageCounter 2.1.25 [Dec 2013]**
 - [CHANGED]	Disabled orientation change
 - [CHANGED]	Simplified message counts at a glance
 - [OPTIMIZED]	Code and layout
 - [CHANGED]	Text block highlight color
 
**MessageCounter 2.1.28 [Dec 2013]**
  - [CHANGED]	Animation durations
  - [ADDED]		Display draft messages
  - [CHANGED]	Message counts at a glance
  
**MessageCounter 2.1.30 [Dec 2013]**
 - [ADDED]		Preference screen and message from non contact preference
 - [ADDED]		Filter out non contact messages from chart screen
 
Version 2.0 (Qiqirn)
--------------------
**MessageCounter 2.0.57 [May 2013]**
 - [CHANGED]	Min SDK Version is 11, for ViewPager
 - [ADDED]		Using Fragments instead of ViewFlipper
 - [CHANGED]	MainActivity so as to hold data for fragments
 - [ADDED]		Fragments for Chart and List
 - [ADDED]		List View is now Sorted based on messages sent by each user 
 - [CHANGED]	Circular Progress bar style
 - [REMOVED]	Removed Progress bars from fragments
 - [CHANGED]	Added Progress Dialog in MainActivity 
 - [ADDED]		Added Apache Licence, Version 2.0
 
**MessageCounter 2.0.64 [Jul 2013]**
 - [OPTIMIZED]	General Optimizations 
 - [ADDED]		Added license popup
 - [FIXED]		No data shown on orientation change
 - [ADDED]		Added title with count of top message senders for the chart fragment
 - [ADDED]		Added an imporved icon for the launcher
 - [ADDED]		Minor styles for total message count
 - [CHANGED]	Reworded some strings to make it better
 - [REMOVED]	Removed some debug log statements

**MessageCounter 2.0.68 [Jul 2013]**
 - [CHANGED]	Change in the way contact info is read from sms table in the library
 - [CHANGED]	Minor optimizations for Chart Display page
 - [FIXED]		Fixed a chart value calculation error
 
**MessageCounter 2.0.80 [Jul 2013]**
 - [FIXED]		Project's name for AppStore
 - [FIXED]		Messages from same contact using multiple address was duplicated
 - [CHANGED]	Changed the default contact image to platform default
 - [OPTIMIZED]	Optimizations
 
**MessageCounter 2.0.84 [Jul 2013]**
 - [ADDED]		Sent message count
 - [OPTIMIZED]	Optimizations
 
**MessageCounter 2.0.94 [Jul 2013]**
 - [FIXED]		Fix for Chart Size issues
 - [ADDED]		Text describing Other Senders in Chart Page
 - [ADDED]		Share app added in the menu 
 - [ADDED]		Experimental support for French language
 - [ADDED]		Section Heading styles for section headings
 
**MessageCounter 2.0.96 [Aug 2013]**
 - [CHANGED]	Color theme for chart
 - [CHANGED]	Chart starts from 12'O clock position
 - [CHANGED]	Start of Test in chart getting clipped  

**MessageCounter 2.0.99 [Aug 2013]**
 - [CHANGED]	Built against API 18
 - [CHANGED]	Colors in chart
 - [ADDED]		Share this app
 - [ADDED]		Support for large screens	
 
**MessageCounter 2.0.102 [Nov 2013]**
 - [ADDED]		Support for Spanish language 
 - [ADDED]		Share icon in actionbar
 

Version 1.2 (Pegasus)
-----------------------
**MessageCounter 1.2.20 [Jan 2013]**
 - [FIXED]		Null pointer caused by null contactId
 - [CHANGED]	Reverted to BaseAdapter as base class for the list
 
**MessageCounter 1.2.28 [Jan 2013]**
 - [FIXED]		Multiple bug fixes
 - [CHANGED]	Added methods in ContactManager to fetch contactId from rawContactId
 
**MessageCounter 1.2.30 [Jan 2013]**
 - [ADDED]		Sort message sender's list based on count
 
**MessageCounter 1.2.40 [Jan 2013]**
 - [ADDED]		Menu options and About app page
 - [ADDED]		Content and layout for About page
 
**MessageCounter 1.2.50 [Feb 2013]**
 - [ADDED]		SimpleGraphView for message counts
 - [ADDED]		Labels for SimpleGraphViews
 - [CHANGED]	Application view set to portrait
 - [ADDED]		Removed the ToggleButton to switch views and instead using a ViewFlipper
 - [ADDED]		Fix for exception in Chart view if there are no messages
 - [CHANGED]	Change in Layout
 
**MessageCounter 1.2.53 [Feb 2013]**
 - [FIXED]		Contact images for contacts without images
 - [FIXED]		Errors in calculation of Message Counts for chart
 
**MessageCounter 1.2.58 [Feb 2013]**
 - [ADDED]		QuickContactBadge instead of Contact Image
 - [ADDED]		MaxRowsCount for Chart
 - [ADDED]		Color themes for the chart in library
 - [FIXED]		Correction for message count calculation
 - [ADDED]		Inbox as well as all message count
 - [CHANGED]	Sorted the list in the chart based on the count

Version 1.1 (Orthrus)
-----------------------
**MessageCounter 1.1.1 [Jan 2013]**
 - [ADDED]		Initial Code Setup
 - [CHANGED]	Fetch the number of messages in inbox
 - [ADDED]		Using AeAppsLibrary for common code