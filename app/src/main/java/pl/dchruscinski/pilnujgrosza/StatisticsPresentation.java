package pl.dchruscinski.pilnujgrosza;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;

public class StatisticsPresentation extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    String activityTitle, startDate, endDate, noDataText;
    BigDecimal numberOfIncomes, numberOfExpenses, numberOfTransactions, balance, sumOfIncomes, sumOfExpenses,
            averageIncome, averageExpense, averageIncomePerMonth, averageExpensePerMonth, averageCost, maxIncome, maxExpense, numberOfMonths;
    List<Map> profileStringStatistics = new ArrayList<>();
    List<Map> profilePieChartGStatistics = new ArrayList<>();
    Map<String, BigDecimal> bigIntegerStringStatistics = new HashMap<>();
    Map<String, String> stringStringStatistics = new HashMap<>();
    Map<String, BigDecimal> incomePieChartStatistics = new HashMap<>();
    Map<String, BigDecimal> expensePieChartStatistics = new HashMap<>();
    TextView startDateTextView, endDateTextView, numberOfTransactionsTextView, balanceCurrencyTextView, numberOfIncomesTextView, numberOfExpensesTextView, balanceTextView, sumOfIncomesTextView, sumOfExpensesTextView, averageIncomeTextView,
            averageExpenseTextView, averageIncomePerMonthTextView, averageExpensePerMonthTextView, averageCostTextView, maxIncomeTextView, maxExpenseTextView, numberOfMonthsTextView, titleTextView,
            sumOfIncomesCurrencyTextView, sumOfExpensesCurrencyTextView, averageIncomeCurrencyTextView, averageExpenseCurrencyTextView, averageIncomePerMonthCurrencyTextView, averageExpensePerMonthCurrencyTextView,
            averageCostCurrencyTextView, maxIncomeCurrencyTextView, maxExpenseCurrencyTextView;
    PieChart incomePieChart, expensePieChart;
    Boolean areIncomePieChartValuesPercentage, areExpensePieChartValuesPercentage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_presentation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);

        ArrayList<Integer> dataSetColors = new ArrayList<>();

        dataSetColors.add(Color.rgb(255, 102, 102)); // 3 1 1
        dataSetColors.add(Color.rgb(102, 255, 102)); // 1 3 1
        dataSetColors.add(Color.rgb(102, 255, 255)); // 1 1 3

        dataSetColors.add(Color.rgb(255, 102, 178)); // 3 1 2
        dataSetColors.add(Color.rgb(178, 255, 102)); // 2 3 1
        dataSetColors.add(Color.rgb(102, 178, 255)); // 1 2 3

        dataSetColors.add(Color.rgb(255, 178, 102)); // 3 2 1
        dataSetColors.add(Color.rgb(178, 102, 255)); // 2 1 3
        dataSetColors.add(Color.rgb(102, 255, 178)); // 1 3 2

        dataSetColors.add(Color.rgb(255, 102, 255)); // 3 1 3
        dataSetColors.add(Color.rgb(255, 255, 102)); // 3 3 1
        dataSetColors.add(Color.rgb(102, 255, 255)); // 1 3 3

        dataSetColors.add(Color.rgb(178, 102, 102)); // 2 1 1
        dataSetColors.add(Color.rgb(102, 178, 102)); // 1 2 1
        dataSetColors.add(Color.rgb(102, 102, 178)); // 1 1 2

        dataSetColors.add(Color.rgb(255, 255, 102)); // 3 3 1
        dataSetColors.add(Color.rgb(102, 255, 255)); // 1 3 3
        dataSetColors.add(Color.rgb(255, 102, 255)); // 3 1 3

        if (getIntent().getExtras() != null && getIntent().getStringExtra("type").equals("overall")) {
            try {
                profileStringStatistics = databaseHelper.getProfileStatistics();
                bigIntegerStringStatistics = profileStringStatistics.get(0);
                stringStringStatistics = profileStringStatistics.get(1);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            startDate = stringStringStatistics.get("startDate");
            endDate = stringStringStatistics.get("endDate");

            numberOfTransactions = bigIntegerStringStatistics.get("numberOfTransactions");
            numberOfIncomes = bigIntegerStringStatistics.get("numberOfIncomes");
            numberOfExpenses = bigIntegerStringStatistics.get("numberOfExpenses");
            balance = bigIntegerStringStatistics.get("balance");
            sumOfIncomes = bigIntegerStringStatistics.get("sumOfIncomes");
            sumOfExpenses = bigIntegerStringStatistics.get("sumOfExpenses");
            averageIncome = bigIntegerStringStatistics.get("averageIncome");
            averageExpense = bigIntegerStringStatistics.get("averageExpense");
            numberOfMonths = bigIntegerStringStatistics.get("numberOfMonths");
            averageIncomePerMonth = bigIntegerStringStatistics.get("averageIncomePerMonth");
            averageExpensePerMonth = bigIntegerStringStatistics.get("averageExpensePerMonth");
            averageCost = bigIntegerStringStatistics.get("averageCost");
            maxIncome = bigIntegerStringStatistics.get("maxIncome");
            maxExpense = bigIntegerStringStatistics.get("maxExpense");

            balance = balance.divide(BigDecimal.valueOf(100));
            sumOfIncomes = sumOfIncomes.divide(BigDecimal.valueOf(100));
            sumOfExpenses = sumOfExpenses.divide(BigDecimal.valueOf(100));
            averageIncome = averageIncome.divide(BigDecimal.valueOf(100));
            averageExpense = averageExpense.divide(BigDecimal.valueOf(100));
            averageIncomePerMonth = averageIncomePerMonth.divide(BigDecimal.valueOf(100));
            averageExpensePerMonth = averageExpensePerMonth.divide(BigDecimal.valueOf(100));
            averageCost = averageCost.divide(BigDecimal.valueOf(100));
            maxIncome = maxIncome.divide(BigDecimal.valueOf(100));
            maxExpense = maxExpense.divide(BigDecimal.valueOf(100));

            startDateTextView = findViewById(R.id.stat_pres_startDate);
            endDateTextView = findViewById(R.id.stat_pres_endDate);
            titleTextView = findViewById(R.id.stat_pres_title);
            numberOfTransactionsTextView = findViewById(R.id.stat_pres_numberOfTransactions);
            balanceTextView = findViewById(R.id.stat_pres_balance);
            balanceCurrencyTextView = findViewById(R.id.stat_pres_balance_currency);
            numberOfIncomesTextView = findViewById(R.id.stat_pres_numberOfIncomes);
            numberOfExpensesTextView = findViewById(R.id.stat_pres_numberOfExpenses);
            sumOfIncomesTextView = findViewById(R.id.stat_pres_sumOfIncomes);
            sumOfIncomesCurrencyTextView = findViewById(R.id.stat_pres_sumOfIncomes_currency);
            sumOfExpensesTextView = findViewById(R.id.stat_pres_sumOfExpenses);
            sumOfExpensesCurrencyTextView = findViewById(R.id.stat_pres_sumOfExpenses_currency);
            averageIncomeTextView = findViewById(R.id.stat_pres_averageIncome);
            averageIncomeCurrencyTextView = findViewById(R.id.stat_pres_averageIncome_currency);
            averageExpenseTextView = findViewById(R.id.stat_pres_averageExpense);
            averageExpenseCurrencyTextView = findViewById(R.id.stat_pres_averageExpense_currency);
            numberOfMonthsTextView = findViewById(R.id.stat_pres_numberOfMonths);
            averageIncomePerMonthTextView = findViewById(R.id.stat_pres_averageIncomePerMonth);
            averageIncomePerMonthCurrencyTextView = findViewById(R.id.stat_pres_averageIncomePerMonth_currency);
            averageExpensePerMonthTextView = findViewById(R.id.stat_pres_averageExpensePerMonth);
            averageExpensePerMonthCurrencyTextView = findViewById(R.id.stat_pres_averageExpensePerMonth_currency);
            averageCostTextView = findViewById(R.id.stat_pres_averageCost);
            averageCostCurrencyTextView = findViewById(R.id.stat_pres_averageCost_currency);
            maxIncomeTextView = findViewById(R.id.stat_pres_maxIncome);
            maxIncomeCurrencyTextView = findViewById(R.id.stat_pres_maxIncome_currency);
            maxExpenseTextView = findViewById(R.id.stat_pres_maxExpense);
            maxExpenseCurrencyTextView = findViewById(R.id.stat_pres_maxExpense_currency);


            activityTitle = "Statystyki ogólne dla profilu: " + databaseHelper.getProfile(chosenProfileID).getProfName();
            titleTextView.setText(activityTitle);

            startDateTextView.setText(startDate);
            endDateTextView.setText(endDate);
            numberOfTransactionsTextView.setText(String.valueOf(numberOfTransactions));
            balanceCurrencyTextView.setText(databaseHelper.getCurrency(chosenProfileID));
            numberOfIncomesTextView.setText(String.valueOf(numberOfIncomes));
            numberOfExpensesTextView.setText(String.valueOf(numberOfExpenses));
            balanceTextView.setText(String.valueOf(balance));
            sumOfIncomesTextView.setText(String.valueOf(sumOfIncomes));
            sumOfIncomesCurrencyTextView.setText(databaseHelper.getCurrency(chosenProfileID));
            sumOfExpensesTextView.setText(String.valueOf(sumOfExpenses));
            sumOfExpensesCurrencyTextView.setText(databaseHelper.getCurrency(chosenProfileID));
            averageIncomeTextView.setText(String.valueOf(averageIncome));
            averageIncomeCurrencyTextView.setText(databaseHelper.getCurrency(chosenProfileID));
            averageExpenseTextView.setText(String.valueOf(averageExpense));
            averageExpenseCurrencyTextView.setText(databaseHelper.getCurrency(chosenProfileID));
            numberOfMonthsTextView.setText(String.valueOf(numberOfMonths));
            averageIncomePerMonthTextView.setText(String.valueOf(averageIncomePerMonth));
            averageIncomePerMonthCurrencyTextView.setText(databaseHelper.getCurrency(chosenProfileID));
            averageExpensePerMonthTextView.setText(String.valueOf(averageExpensePerMonth));
            averageExpensePerMonthCurrencyTextView.setText(databaseHelper.getCurrency(chosenProfileID));
            averageCostTextView.setText(String.valueOf(averageCost));
            averageCostCurrencyTextView.setText(databaseHelper.getCurrency(chosenProfileID));
            maxIncomeTextView.setText(String.valueOf(maxIncome));
            maxIncomeCurrencyTextView.setText(databaseHelper.getCurrency(chosenProfileID));
            maxExpenseTextView.setText(String.valueOf(maxExpense));
            maxExpenseCurrencyTextView.setText(databaseHelper.getCurrency(chosenProfileID));

            profilePieChartGStatistics = databaseHelper.getProfileStatisticsForPieChart();
            incomePieChartStatistics = profilePieChartGStatistics.get(0);
            expensePieChartStatistics = profilePieChartGStatistics.get(1);
            ArrayList<BigDecimal> incomeValuesList = new ArrayList<>(incomePieChartStatistics.values());
            ArrayList<String> incomeKeysList = new ArrayList<>(incomePieChartStatistics.keySet());
            ArrayList<BigDecimal> expenseValuesList = new ArrayList<>(expensePieChartStatistics.values());
            ArrayList<String> expenseKeysList = new ArrayList<>(expensePieChartStatistics.keySet());

            noDataText = "Brak danych";

            incomePieChart = findViewById(R.id.stat_pres_inc_pieChart);
            incomePieChart.setBackgroundColor(Color.WHITE);
            incomePieChart.setUsePercentValues(true);
            areIncomePieChartValuesPercentage = true;
            incomePieChart.setDrawHoleEnabled(true);
            incomePieChart.setNoDataText(noDataText);
            incomePieChart.getDescription().setEnabled(false);
            incomePieChart.setEntryLabelColor(Color.BLACK);

            expensePieChart = findViewById(R.id.stat_pres_exp_pieChart);
            expensePieChart.setBackgroundColor(Color.WHITE);
            expensePieChart.setUsePercentValues(true);
            areExpensePieChartValuesPercentage = true;
            expensePieChart.setDrawHoleEnabled(true);
            expensePieChart.setNoDataText(noDataText);
            expensePieChart.getDescription().setEnabled(false);
            expensePieChart.setEntryLabelColor(Color.BLACK);

            if (incomeValuesList.size() > 0) {
                setData(incomeValuesList, incomeKeysList, "Katgorie przychodów", incomePieChart, dataSetColors);
            }
            if (expenseValuesList.size() > 0) {
                setData(expenseValuesList, expenseKeysList, "Katgorie wydatków", expensePieChart, dataSetColors);
            }

            Legend incomePieChartLegend = incomePieChart.getLegend();
            incomePieChartLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            incomePieChartLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            incomePieChartLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            incomePieChartLegend.setDrawInside(false);
            incomePieChartLegend.setWordWrapEnabled(true);

            Legend expensePieChartLegend = expensePieChart.getLegend();
            expensePieChartLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            expensePieChartLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            expensePieChartLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            expensePieChartLegend.setDrawInside(false);
            expensePieChartLegend.setWordWrapEnabled(true);



            incomePieChart.setOnChartGestureListener(new OnChartGestureListener() {
                @Override
                public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartLongPressed(MotionEvent me) {

                }

                @Override
                public void onChartDoubleTapped(MotionEvent me) {

                }

                @Override
                public void onChartSingleTapped(MotionEvent me) {
                    if (areIncomePieChartValuesPercentage) {
                        incomePieChart.setUsePercentValues(false);
                        areIncomePieChartValuesPercentage = false;
                    } else {
                        incomePieChart.setUsePercentValues(true);
                        areIncomePieChartValuesPercentage = true;
                    }
                }

                @Override
                public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

                }

                @Override
                public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

                }

                @Override
                public void onChartTranslate(MotionEvent me, float dX, float dY) {

                }
            });

            expensePieChart.setOnChartGestureListener(new OnChartGestureListener() {
                @Override
                public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartLongPressed(MotionEvent me) {

                }

                @Override
                public void onChartDoubleTapped(MotionEvent me) {

                }

                @Override
                public void onChartSingleTapped(MotionEvent me) {
                    if (areExpensePieChartValuesPercentage) {
                        expensePieChart.setUsePercentValues(false);
                        areExpensePieChartValuesPercentage = false;
                    } else {
                        expensePieChart.setUsePercentValues(true);
                        areExpensePieChartValuesPercentage = true;
                    }

                }

                @Override
                public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

                }

                @Override
                public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

                }

                @Override
                public void onChartTranslate(MotionEvent me, float dX, float dY) {

                }
            });

        } else if (getIntent().getExtras() != null && getIntent().getStringExtra("type").equals("budget")) {
            int budID = getIntent().getExtras().getInt("budID");


        } else if (getIntent().getExtras() != null && getIntent().getStringExtra("type").equals("monthly")) {
            String monthDate = getIntent().getExtras().getString("month");


        } else if (getIntent().getExtras() != null && getIntent().getStringExtra("type").equals("period")) {
            String startDate = getIntent().getExtras().getString("startDate");
            String endDate = getIntent().getExtras().getString("endDate");


        }


    }

    private void setData(ArrayList<BigDecimal> valuesList, ArrayList<String> keysList, String label, PieChart pieChart, ArrayList<Integer> dataSetColors) {
        ArrayList<PieEntry> values = new ArrayList<>();

        for (int i = 0; i < valuesList.size(); i++) {
            values.add(new PieEntry(valuesList.get(i).floatValue(), keysList.get(i)));
        }

        PieDataSet dataSet = new PieDataSet(values, "");
        dataSet.setSelectionShift(0f);
        dataSet.setSliceSpace(3f);
        dataSet.setColors(dataSetColors);

        PieData pieChartData = new PieData(dataSet);
        pieChartData.setValueFormatter(new PercentFormatter(pieChart));
        pieChartData.setValueTextSize(15f);
        pieChartData.setValueTextColor(Color.BLACK);

        pieChart.setData(pieChartData);
        pieChart.invalidate();

    }

}