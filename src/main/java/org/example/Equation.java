package org.example;

public class Equation {
    public static double[] CalculatePartitionFunction(double[] epsilon) {
        double K = (8.314 * 0.001) / 4.18;  // ideal gas constant in kcal/mol*k
        double c = epsilon[0];  // Chosen input energy levels
        double T = 310;    // Temperature
        double[] Energy_Differences = new double[epsilon.length];
        double[] X_Values = new double[epsilon.length];

        for (int i = 0; i < epsilon.length; ++i) {
            Energy_Differences[i] = epsilon[i] - c;
            //System.out.println(energyDifferences[i]);
            double DividedValue = Energy_Differences[i] / (K * T);
            //System.out.println("Energy difference " + (i + 1) + ": " + dividedValue);
            X_Values[i] = Math.exp(-DividedValue);
            //System.out.println("Energy difference " + (i + 1) + ": " + xValues[i]);
        }

        double q;  // partition function
        try {
            q = SumKahan(X_Values);
        } catch (Exception e) {
            System.err.println("Error in summation using Kahan algorithm: " + e.getMessage());
            e.printStackTrace();
            return new double[0]; // Return an empty array to indicate failure
        }

        double[] Normalized_X_Values = new double[X_Values.length];

        for (int i = 0; i < X_Values.length; ++i) {
            Normalized_X_Values[i] = X_Values[i] / q;
        }

        return Normalized_X_Values;
    }

    public static double SumKahan(double[] y) {
        double Sum = 0.0;
        double Error = 0.0;
        for (double v : y) {
            double value = v - Error;
            double New_Sum = Sum + value;
            Error = (New_Sum - Sum) - value;
            Sum = New_Sum;
        }
        return Sum;
    }
}


/*
Additional Details:
THEORY:
// Boltzmann
double NiOverN = Math.exp(-epsilon_i / (k * T)) / sum;
double sum = 0.0;
for (int j = 1; j <= M; j++) {
    sum += Math.exp(-epsilon_j / (k * T));
}
 */
/*
// Kahan
int numberofValues = 10000000;
double eps = Math.ulp(1.0);
double[] realization = new double[numberofValues];
Arrays.fill(realization,eps);
realization[0]= 1.0;

public class double GetSumKahan(double[] realization) {
   double sum=0.0;
   double error=0.0;
   for (int i=0; i<realization.length; i++) {
      double value = realization[i] - error;
      double newSum = sum + value;
      error=(newSum-sum)-value;
      sum=newSum;
   }
   return sum;
}

double SumOfValuesKahan=GetSumKahan(realization);
 */







