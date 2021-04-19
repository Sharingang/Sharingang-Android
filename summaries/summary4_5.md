# Week 4 and 5

## Bastien
During the first week, I worked on taking pictures with the app to add the image to an item. Making the CI pass was a bit of a hassle and took me quite some time which is why my time estimate for this task was not on point for this task.

During the second week, I added the link between an item and a user, and a way to have access to the user profile of the person that posted the item.
I also fixed a bug that was making the image disappear if we edited an item without changing its image.

My estimates were relatively good for this week.

## Benedek
What I did these two sprints was (1) to implement the persistence of the user login. Before, when a user logged in, when they left the activity, they had to log in again. Now, the user is stored in Firestore and they are kept across the whole app, and (2) to implement the possibility of changing the profile picture. However, for some unknown reason, the user profile data is not updated in Firestore, and this issue is still in a state of work in progress to figure out what might be wrong. (working on it with Samuel)

## Lúcás
For the first week, I managed to replace our ugly buttons with a navigation bar. I estimated that it would take me a long time (8 hours) which it ended up taking. This was because I had to figure out the necessary Android incantations, as well as re-work all of the tests we had to use navigation, instead of the buttons.

For the second week, I decided to implement seeing images inside of the detailed view for items. This took me a bit less time than estimated (6 hours vs 8 hours). The main hitch was figuring out how to use the existing framework we had concocted for requesting image permissions, and fiddling around with testing. I think this would've taken me the 8 hours had this framework not existed.

I think my estimates are relatively good, but perhaps a bit too conservative.

## Samuel
During this longer sprint, I implemented the deeplinks (i.e. a share button on the detail view that constructs a URL to this item and opens a sharing popup and handling these links to open the detail view of the item when it is clicked).
It took longer than expected to implement the tests because I couldn’t find any documentation explaining how to catch the share intent from the tests.

I haven't worked on another user story, but I helped others with dependency injection to test the app more easily and also for refactoring to simplify the code and increase code coverage.
During the last week I did less work because I was busy with my semester project for which I had a presentation this morning.

## Sebastian
I continued working on the map and map features.
I implemented a way to display the items on the map, with blue markers, and added a way to click on them to display their full description.

I didn’t encounter any difficulties along the way.

As for my time estimates, I managed to do those tasks quicker than I imagined.

## Zach
This week I implemented the basic search algorithm, both for text and for categories. Went through a few different algorithms and still needs improvement but for now it works. This work took be about 9 hours altogether.

Next week I want to improve the implementation and try to implement a chat system.

My time estimations are getting better but I would like to implement tasks that require a bit more learning and research.

## Overall team
We did three stand-up meetings during this two week long sprint: (1st week) Wednesday, (2nd week) Tuesday and Thursday.

The first one was not really useful as most of the members hadn't worked on the project yet because of midterms and other project reports at the beginning of the week.

Everyone participated in at least a review.
