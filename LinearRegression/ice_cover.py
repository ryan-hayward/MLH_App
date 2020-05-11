# Author: Ryan Hayward
# Email: rchayward@wisc.edu
# Title: Ice Cover Regression


# Program sharpened by understanding of using multiple forms of gradient descent
# to find a most-fit regression for a data set. Stochastic Gradient Descent (SGD)
# and Iterative Gradient Descent are used to fit ice coverage data for Madison's
# Lake Mendota (in days per year) to a linear regression


import csv
import random
import statistics as stat


# takes no arguments and returns the data as described below in an n-by-2 array
def get_dataset():
    clean_data = []
    # File path is unique to my system. You will need to update if running on your own device by
    # finding the absolute path of the .csv file
    filepath = "/Users/rchayward/cs540/p8/CleanIceData.csv"
    # use same technique as used in P7
    with open(filepath, newline='') as raw_csv:
        dic_reader = csv.DictReader(raw_csv)
        for row in dic_reader:
            yr_data = []
            year = row['\ufeffWINTER']  # add year
            year = year[:4]  # trim second year off of year interval
            yr_data.append(year)  # add year to data list
            days_frozen = row['DAYS']  # add count
            days_frozen = ''.join(x for x in days_frozen if x.isdecimal())  # trim non-decimal values
            yr_data.append(days_frozen)  # add days frozen to data list
            clean_data.append(yr_data)  # add to master list
    return clean_data


# print expected values for size, mean, and std dev of ice cover
def print_stats(dataset):
    print(len(dataset))
    data = []  # format data with loop
    for year in dataset:
        data.append(int(year[1]))
    # round to 2 decimal places on each
    print('{:.2f}'.format(stat.mean(data)))
    print('{:.2f}'.format(stat.stdev(data)))


# Use equation 1/n Sum(beta_0 - beta_1(x) - y)**2 to find Mean Squared Error regression.
def regression(beta_0, beta_1, data=get_dataset()):
    reg_list = []  # list of regression values
    for row in data:  # calculate y for each year given equation
        year = float(row[0])
        cover = float(row[1])
        y = beta_0 + (beta_1 * year)
        y = (y - cover) ** 2
        reg_list.append(y)
    # return the proper regression rounded to 2 decimal places
    return sum(reg_list) / len(data)


# performs a single step of gradient descent on the MSE and
# returns the derivative values as a tuple
def gradient_descent(beta_0, beta_1, data=get_dataset()):
    gx = []  # initialize gradient lists
    gy = []
    for row in data:
        x = float(row[0])  # year
        y = float(row[1])  # cover
        val1 = beta_0 + (beta_1 * x) - y
        gx.append(val1)  # append value to gx list
        val2 = (beta_0 + (beta_1 * x) - y) * x
        gy.append(val2)  # append value to gy list
    # plug summations into the formula and round to two decimal places
    rx = ((sum(gx)) / len(data) * 2)
    ry = ((sum(gy)) / len(data) * 2)
    # return with proper formatting
    return float(rx), float(ry)


# performs T iterations of gradient descent starting at beta(0), beta(1) = 0,0
# with the given parameter and prints the results; does not return anything
def iterate_gradient(T, eta, data=get_dataset()):
    t = 1  # starting T value (will increment)
    previous_b0 = 0  # starting beta values
    previous_b1 = 0
    for i in range(0, T):  # loop through T's, implement formula
        new_betas = gradient_descent(previous_b0, previous_b1, data)  # call gradient descent
        beta_0 = previous_b0 - eta * (new_betas[0])
        beta_1 = previous_b1 - eta * (new_betas[1])
        mse = regression(beta_0, beta_1, data)
        # ROUNDING OCCURS IN THIS PRINT STATEMENT
        print(t, '{:.2f}'.format(beta_0), '{:.2f}'.format(beta_1), '{:.2f}'.format(mse))
        # update starting betas and t value for next loop
        previous_b0 = beta_0
        previous_b1 = beta_1
        t = t + 1


# using the closed-form solution, calculates and returns the values of
# beta(0) and beta(1) and the corresponding MSE as a three-element tuple
def compute_betas():
    data = get_dataset()
    years = []
    coverage = []
    for row in data:
        years.append(int(row[0]))
        coverage.append(int(row[1]))
    x_bar = sum(years) / len(data)
    y_bar = sum(coverage) / len(data)
    # compute beta(1) first
    num = []  # numerator list to sum
    denom = []  # denominator list to sum
    for row in data:
        n = (int(row[0]) - x_bar) * (int(row[1]) - y_bar)
        num.append(n)  # append calculated numerator
        d = (int(row[0]) - x_bar) ** 2
        denom.append(d)  # append calculated denominator
    beta_1 = sum(num) / sum(denom)
    beta_0 = y_bar - (beta_1 * x_bar)
    mse = regression(beta_0, beta_1)
    return beta_0, beta_1, mse


# using the closed-form solution betas, returns the predicted number
# of ice days for a given (int) year
def predict(year):
    c_form = compute_betas()  # get closed-form betas
    beta_0 = float(c_form[0])
    beta_1 = float(c_form[1])
    return '{:.2f}'.format(beta_0 + (beta_1 * year))  # b0 + b1*X


# normalizes the data before performing gradient descent,
# prints results as in function 5 (USES iterate_gradient
# to print results, passing adjusted dataset through
def iterate_normalized(T, eta):
    data = get_dataset()  # load in dataset
    years = []  # get x values
    for row in data:
        years.append(float(row[0]))
    # calculate mean and std dev for years
    x_bar = sum(years) / len(data)
    std_x = stat.stdev(years)
    normalized_data = []  # list of normalized x's
    for row in data:
        new_pair = []
        new_x = (float(row[0]) - x_bar) / std_x
        new_pair.append(new_x)  # append normalized x
        new_pair.append(row[1])  # also append old y
        normalized_data.append(new_pair)
    iterate_gradient(T, eta, normalized_data)

# performs stochastic gradient descent, prints results as
# in function 5. Like iterate_normalized, uses iterate_gradient()
# to print values
def sgd(T, eta):
    data = get_dataset()  # load in dataset
    years = []  # get x values
    for row in data:
        years.append(float(row[0]))
    # calculate mean and std dev for years
    x_bar = sum(years) / len(data)
    std_x = stat.stdev(years)
    normalized_data = []  # list of normalized x's
    # Normalize data in the same way as iterate_normalized()
    for row in data:
        new_pair = []
        new_x = (float(row[0]) - x_bar) / std_x
        new_pair.append(new_x)  # append normalized x
        new_pair.append(row[1])  # also append old y
        normalized_data.append(new_pair)
    # Copied from iterate_gradient()
    t = 1  # starting T value (will increment)
    beta_0 = 0  # starting beta values
    beta_1 = 0
    for i in range(0, T):  # loop through T's, implement formula
        r_num = random.randint(0, len(data) - 1)  # set random number
        x_rand = float(normalized_data[r_num][0])  # get random x, y pair
        y_rand = float(normalized_data[r_num][1])
        # calculate gradients
        gradient_b0 = 2 * (beta_0 + (beta_1 * x_rand) - y_rand)
        gradient_b1 = gradient_b0 * x_rand
        # calculate betas
        beta_0 = beta_0 - eta * (gradient_b0)
        beta_1 = beta_1 - eta * (gradient_b1)
        mse = regression(beta_0, beta_1, normalized_data)  # calculate mse
        # ROUNDING OCCURS IN THIS PRINT STATEMENT
        print(t, '{:.2f}'.format(beta_0), '{:.2f}'.format(beta_1), '{:.2f}'.format(mse))
        # update t value for next loop
        t = t + 1


# Runs Stochastic Gradient Descent and Iterative Gradient Descent (on normalized data)
# and prints their outputs. Both descent mechanisms should approach an MSE of near 295
# after about 300 iterations. The final print statement produces the predicted ice
# coverage days for a given year (following a linear regression pattern).
if __name__ == "__main__":
    # sgd(500, 0.01)
    # iterate_normalized(100, 0.1)
    # print(predict(5134643))
    print(get_dataset())


