
-Purpose:
Boltzmann+Kahan calculation
Input:.csv file
Output:.xlsx/.txt


-Requirements:
Java 25 or higher   if you are going to be using super()

Intellij(Maven) + Apache POI library

-Theory:
boltzmann+kahan algorithms
precision tolerance is set to 0.000001 = 10^-6
merge sort

| Worst Case time complexity   | O(nlogn) |

| Best Case time complexity    | O(nlogn) |

| Average case time complexity | O(nlogn) |

| Space Complexity             | O(n)     |

| Stable Sorting               | Yes      |

Additional Information:
In Microsoft Excel, Press File->Save as -> .csv
There is no need for a multiple TextToCSV converter it can be done in under a minute by dragging and dropping in Excel.

Future Direction:
Package can be imported for Entropy calculation:
S = k ln (W)
S: Entropy of the system
k: Boltzmann's constant
ln W: Natural logarithm of the number of microstates

CMD:
java -cp "C:\Users\saleh\Downloads\New folder (9)\poi\*;C:\Users\saleh\javaSamples\21\MB\target\classes" org.example.Main
