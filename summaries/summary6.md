# Week 6

## Bastien
I added the list of all the items a user posted on their profile, so you can quickly see what you posted as well as see all the items another user posted. This took me a little longer than planned, because there were a few merge conflicts with another PR whis week.
I then fixed a small bug where the buttons on the search fragment didn't work.
Finally I also made changes so the edit and sell buttons on an item would only be visible to the user that actually posted these items.

My estimates were almost on point for this week.

## Benedek
This week, I have been working on finishing the Profile Picture draft from last week. Now the user can only modify their own profile picture. I have also made a small fix for the user profile: Before, an offline user could not see anything related to the user profile they want to view. Now, they are able to see it (however, they cannot report the user as they are not logged in). I also worked on the reporting of users. Logged-in users can click the "report" button when they go on a user profile. With this action, the report is saved into a set of reports for that particular user, which potential moderators can review later on.

## Lúcás
This week the main task I focused on was integrating search results with the map.
I decided to change the behavior to use the current items searched for, instead
of displaying all of the items. To make this simpler for the user, I added extra
navigation paths between the map and search.

This took me less time thatn estimated, because I didn't know how easy it would
be to do things with the map. Thankfully, I only had to make a few incremental
changes to the code that was already there. I think it would have taken me more time
if that code wasn't already there.

My estimates were a bit too conservative this week, but I was working on a new
part of the app: the map stuff.


## Samuel
This week I worked more on the UI to change a bit.
I added custom markers on the map that display the title and price of the item without having to click on it.
Also if there are too many items, they are clustered together until the user zooms more.
I also created a class to upload images to Firebase, this PR hasn't been merged yet because the class isn't used yet so the coverage is too low.
Next sprint Zach will continue this and update the existing features to use it.
I spent a bit more time than expected for the markers but my estimation for the image upload on Firebase was accurate.

## Sebastian
Right after last week’s meeting, I fixed a bug that caused the app to crash whenever the user clicked twice on deny the location permission request.

Since writing the coordinates is quite unconvenient, I implemented a way to add postal addresses instead, which suggestions just like we could see in GoogleMaps. 

Finally, I merged two fragments together as they were quite similar: EditItemFragment and NewItemFragment, to avoid copy pasted code.

I didn’t encounter any difficulties along the way, and my time estimates were a bit inaccurate.

## Zacharie
The week I worked on finishing the wish list.
I then took care of the rating functionality and view for the application.

Next week I intend on working with Sam on finishing the image storage on firebase.

I didn't encounter many difficulties and my time estimates were fairly accurate.

## Overall team
We did 2 stand-up meetings, the first one on Tuesday and the second on Thursday.
The first meeting was mostly to figure out who had started which task and which ones they intended to finish by the end of the week.
The second one went smoothly and was mostly used to complete most of the remaining pull requests.
Everyone participated in a least a review.