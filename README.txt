Michael Ing - Hwk 2

1. I did binarization by keeping a running count of the total number of rules and how many extra rules were added. 
	For each rule that had more than 2 children, I looped through and added a new rule for every extra argument above 2,
	that is 1 extra rule for 3 children, 2 extra rules for 4 children, etc. I gave all extra rules a prob of 1.0 so that 
	they wouldn't affect probability. I also included an "original" parameter so that when they were looped through and found
	to not be original rules, they were simply ignored in the s-class printing.
	
2. The only known issue is that I got a Java error that the name of the class couldn't be "cs2731.hw2.ProbCKY" as instructed,
	so I instead named the class "cs2731_hw2_ProbCKY".