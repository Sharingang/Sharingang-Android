# Week 6

## Bastien
This week I added buttons in the AppBar on the detailed view to sell, unsell and go to the edit fragment. 
Adding these buttons was pretty tricky because following the documentation didn't work and the order of the 
call to transform our Toolbar in an AppBar mattered, otherwise the mennu and up arrow would not work anymore.
Finding this problem and solving it took me a very long time which is why my time estimate is off for this task.
I also fixed a problem that we had which was recursive navigation and made it so after editing an item, 
we get redirected to the detailed view instead of the home page.

## Benedek
This week, I improved the Report feature, so that we can now report users for a specific reason and then adding
a description to the report, so that moderators will know what is going on. I also added the fact that users cannot
report themselves or users that they have already reported. The main challenge was the second task, getting information
directly from the database this way was something that I have not done before in this project, but it went smoothly overall.
Another difficulty I had to figure out how to test this, as we need two different users authenticated to make sure that
they can report each other only once, and cannot report themselves.
My time estimate for this was not that accurate, as I underestimated the amount of time it would take me to solve these
two challenges above.

## Lúcás
I first implemented a simple feature to see a random item. This was pretty easy, besides figuring out how to correctly access items
in the Main Activity. Then, I started working on displaying items with images. I was actually somewhat blocked
on the fact that our infrastructure for remote images was not ready yet, so I decided
to just display a placeholder image, in order to get something out anyway.
I also ended up fixing a bug stemming from concurrency while working on this feature anyways.
I'd say work was pretty smooth, besides our work on images being a pretty major bottleneck.


## Samuel
This week I added a progress bar to display the saving status and added the ability to delete an item.
The implementation went well but it took me a long time to update the tests because now we must wait 
for the save to complete. Because of that I didn’t have enough time to implement validation of the item creation/edition.
Zacharie finished the integration of Firebase storage for the images so my code from last week can now be merged.

## Sebastian
I implemented the search feature directly on the map, as I thought it was more convenient this way.
I also started implementing a feature to order the items by price, category, name or added date, 
but couldn’t finish it because of merging conflicts with the features implemented by Lucas.
My time estimates were pretty accurate this week.

## Zacharie


## Overall team
We did 2 stand-up meetings, the first one on Tuesday and the second on Thursday.
On Tuesday, we discussed about some difficulties each one of us had and what we have been working on so far. On Thursday,
we discussed some issues that got fixed later on during the day, as well as looking at what kind of user stories we
could make for the next sprint.
Everyone participated in at least a review.
