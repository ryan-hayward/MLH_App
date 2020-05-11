# Author: Ryan Hayward
# Email: rchayward@wisc.edu
# Title: Keras Intro

# This program serves as an introduction to working with Keras and Neural Networks
# in Python.  A set of 60,000 images are used to train the network, and a set of
# 10,000 images are used to test the network's ability to predict the label that
# should be associated with a given 28 x 28 pixel image from a database.


from tensorflow import keras
import matplotlib.pyplot as plt


# takes an optional boolean argument and returns the data as described below.
def get_dataset(training=True):
    fashion_mnist = keras.datasets.fashion_mnist  # prepare testing/training data from a Keras database
    (train_images, train_labels), (test_images, test_labels) = fashion_mnist.load_data()
    if training is False:  # return test or training data
        return test_images, test_labels
    else:
        return train_images, train_labels


# takes the data set and labels produced by the previous function and
# prints several statistics about the data; does not return anything
def print_stats(images, labels):
    # label translations (working with a 'fashion' data set)
    class_names = ['T-shirt/top', 'Trouser', 'Pullover', 'Dress', 'Coat',
                   'Sandal', 'Shirt', 'Sneaker', 'Bag', 'Ankle boot']
    print(len(images))  # get and print image count
    print(images.shape[1], "x", images.shape[2])  # get and print dimension
    list = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]  # list to count label occurrences
    for index in labels:  # count label occurrences
        list[index] = list[index] + 1
    counter = 0  # counter variable
    for item in class_names:  # print off the counts for each item
        print(str(counter) + '.', item, '-', list[counter])
        counter += 1


# takes a single image as an array of pixels and displays an image;
# does not return anything.  Function utilizes matplotlib to display the
# image parameter along with its label
def view_image(image, label):
    figure, ax = plt.subplots(1, 1)  # create a figure
    ax.set_title(label)  # set the title
    view = ax.imshow(image, aspect='equal')  # set the view
    figure.colorbar(view, ax=ax)  # add a color bar
    plt.show()  # show the view


# takes no arguments and returns an untrained
# neural network as specified below
def build_model():
    # 60000 and 28 x 28 are retrieved from print_stats given the training data
    model = keras.Sequential()  # add layers as specified by prompt
    model.add(keras.layers.Flatten(input_shape=(28, 28)))
    model.add(keras.layers.Dense(128, activation='relu'))
    model.add(keras.layers.Dense(10))
    model.compile(loss=keras.losses.SparseCategoricalCrossentropy(from_logits=True),
                  optimizer='adam', metrics=['accuracy'])
    return model  # return the untrained neural net


# takes the model produced by the previous function and the images and
# labels produced by the first function and trains the data for
# T epochs; does not return anything
def train_model(model, images, labels, T):
    model.fit(x=images, y=labels, epochs=T)


# takes the trained model produced by the previous function and the test image/labels,
# and prints the evaluation statistics as described below (displaying the loss metric
# value if and only if the optional parameter has not been set to False)
def evaluate_model(model, images, labels, show_loss=True):
    test_loss, test_accuracy = model.evaluate(images, labels, verbose=0)
    if show_loss is True:  # check 4th argument
        print('Loss:', '{:.2f}'.format(test_loss))
        print('Accuracy:', '{:.2%}'.format(test_accuracy))
    else:
        print('Accuracy:', '{:.2%}'.format(test_accuracy))


# takes the trained model and test images, and prints the top 3 most
# likely labels for the image at the given index, along with their probabilities
def predict_label(model, images, index):
    class_names = ['T-shirt/top', 'Trouser', 'Pullover', 'Dress', 'Coat',
                   'Sandal', 'Shirt', 'Sneaker', 'Bag', 'Ankle boot']
    model.add(keras.layers.Softmax())  # add a softmax layer before predicting
    prediction = model.predict(images)
    list = []  # store dictionaries with class name and probability k,v pairs
    for i in range(0, len(class_names)):
        dic = {'article': class_names[i], 'probability': prediction[index][i]}
        list.append(dic)
    sl = sorted(list, key=lambda e: e['probability'], reverse=True)  # reverse to get top at lowest indexes
    for j in range(0, 3):  # print off top 3 probabilities
        print(sl[j]['article'] + ':', '{:.2%}'.format(sl[j]['probability']))


# Main method to run the program. print_stats() and view_image() calls are commented out
# in order to de-clutter output.
if __name__ == "__main__":
    # Class Name Translations
    class_names = ['T-shirt/top', 'Trouser', 'Pullover', 'Dress', 'Coat',
                   'Sandal', 'Shirt', 'Sneaker', 'Bag', 'Ankle boot']
    (train_images, train_labels) = get_dataset()  # training data
    (test_images, test_labels) = get_dataset(False)  # test data
    # print_stats(test_images, test_labels)  # print statistics (tried both)
    # view_image(train_images[7], class_names[train_labels[7]])  # try with different labels
    model = build_model()
    train_model(model, train_images, train_labels, 5)  # function operates correctly
    evaluate_model(model, test_images, test_labels, True)
    predict_label(model, test_images, 0)

