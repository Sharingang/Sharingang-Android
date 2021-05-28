# Week 10

## Bastien
This week I first worked on adding an intent to the notification so the users would go directly to the page of the new item posted when clicking on it.
This took a little bit longer than expected because I had to receive some help from Samuel to use the Deeplinks he made  to achieve this functionality.
I then added the possibility for users to put their items on sale to attract more buyers.
My time estimates were pretty good this week, I just went a little bit overboard on the first task.

## Ben
First of all, I improved the UI so that it does not look like the default Android UI, and added a global theme to the app. I also corrected two things related to the profile picture: (1) I removed the "Apply" button so that now the user choosing their picture don't need to apply their changes anymore (they are applied straightaway), and (2) I changed the style of the buttons for changing the profile picture to make it more visible to the user (it used to blend in sometimes when the chosen picture was too light/dark). I also added a feature where users can see the number of unread messages they have with another particular users (shown as a red indicator containing the number of unread messages). This feature also implied refactoring the code for the chat as it was not clean and it was depending on Firestore (which might be undesirable), for better modularity. My time estimates were pretty accurate this week, and I managed to finish everything I wanted to do.

## Lúcás
This week I focused on reintegrating and completing the locating information
I started last week. This took me about as much time as I estimated, because
I had to rework our existing permissions for gathering all the sensor data,
and find formulas for doing calculations on the Earth.

I wasn't ever blocked by anything, but my work was pretty independent from
other people. That being said, I did have to do a bit of grunt work to integrate
what I had done after the PR reorganizing every single file in the project

## Sebastian
This week I added a logo to our app, which I think was necessary. I also added security rules for firestore, as before that anyone could do basically anything with our database, it is now fixed. 

The logo took me as much as I expected, because I knew I needed to learn how to use the software (Adobe Illustrator) first. For the latter, since it was my first backend task, I needed to learn how to do it.

## Samuel
This week I have modified the package structure to use a functionality-based hierarchy.  I added validation on the form to create a new item and made mandatory to be signed in create a new item.
I also did two minor PRs, one to fix a UI issue (last row of items was cropped) and the other to move the Firebase cloud functions to the Zurich datacenter (as the other Firebase services) which should decrease the latency.
My time estimate for these tasks was accurate.
Because I'm absent next week, I tried to help the team as much as I could and also reviewed many of their PRs.

## Zacharie
This week I worked on adding quantities to items, this way sellers can sell multiple items at a time and buyers can buy multiple items as well.
I ran into some issue with the ratings since we could now rate an item even though it wasn't in the SOLD state. 
Because of this my time estimates were kind of off. For now the feature works but I believe there is a better way of implementing the ratings using a collection on firestore so I will work on that next week.


## Overall team
As usual, we did two stand-up meetings: one on Monday and the second on Thursday.  The workflow this week was pretty good, as everyone seemed to have independent tasks, and we were able to merge everything without any particular conflict. 
