import pandas as pd
import numpy as np
import re
import string
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer, ENGLISH_STOP_WORDS
from xgboost import XGBClassifier
from sklearn.metrics import accuracy_score, mean_squared_error, r2_score

# Load Dataset
file_path = "/home/user/Documents/KAIBURR/ML-MODEL-TASK-5/complaints.csv"

try:
    df = pd.read_csv(file_path, low_memory=False)
    print("Dataset Loaded Successfully")
except Exception as e:
    print(f"Error Loading Dataset: {e}")
    exit()

# Selecting relevant columns
df = df[["Consumer complaint narrative", "Product"]].dropna()

# Mapping product categories to numeric labels
category_mapping = {
    "Credit reporting, repair, or other": 0,
    "Debt collection": 1,
    "Consumer Loan": 2,
    "Mortgage": 3
}

df = df[df["Product"].isin(category_mapping.keys())]
df["Category"] = df["Product"].map(category_mapping)

# Adjusting category labels to start from 0
df["Category"] = df["Category"] - 1

# Text Preprocessing Function
def clean_text(text):
    if pd.isnull(text):
        return ""
    text = text.lower()  
    text = re.sub(r'\d+', '', text)  
    text = text.translate(str.maketrans('', '', string.punctuation))  
    words = text.split()  
    words = [word for word in words if word not in ENGLISH_STOP_WORDS]  
    return ' '.join(words)

# Apply text cleaning
df["Cleaned_Text"] = df["Consumer complaint narrative"].apply(clean_text)

# Train-Test Split
X_train, X_test, y_train, y_test = train_test_split(
    df["Cleaned_Text"], df["Category"], test_size=0.2, random_state=42)

# Convert text data into numerical vectors using TF-IDF
vectorizer = TfidfVectorizer(max_features=5000)
X_train_tfidf = vectorizer.fit_transform(X_train).toarray()  # Convert sparse matrix to dense
X_test_tfidf = vectorizer.transform(X_test).toarray()

# Optimized XGBoost Model
model = XGBClassifier(
    n_estimators=100,  # Reduced from 300 to 100 for faster training
    learning_rate=0.05,
    max_depth=6,  # Reduced from 8 to 6 to speed up training
    subsample=0.8,
    colsample_bytree=0.8,
    use_label_encoder=False,
    eval_metric='mlogloss',
    n_jobs=-1,  # Utilizes all CPU cores for faster training
)

# Fit the model with early stopping
model.fit(X_train_tfidf, y_train, eval_set=[(X_test_tfidf, y_test)], early_stopping_rounds=10, verbose=True)

# Predictions
y_pred = model.predict(X_test_tfidf)

# Model Evaluation Metrics
accuracy = accuracy_score(y_test, y_pred)
mse = mean_squared_error(y_test, y_pred)
r2 = r2_score(y_test, y_pred)

print("\nModel Performance:")
print(f"Accuracy: {accuracy:.4f}")
print(f"Mean Squared Error (MSE): {mse:.4f}")
print(f"R² Score: {r2:.4f}")

# Predict Categories for First 5 Complaints
df_test_sample = df.iloc[:5].copy()
df_test_sample["Predicted Category"] = model.predict(vectorizer.transform(df_test_sample["Cleaned_Text"]).toarray())

# Reverse Mapping for Category Names
reverse_category_mapping = {v: k for k, v in category_mapping.items()}
df_test_sample["Predicted Category"] = df_test_sample["Predicted Category"].apply(lambda x: reverse_category_mapping[x + 1])

print("\nFirst 5 Complaints and Their Predicted Categories:")
for i in range(5):
    print(f"Complaint {i+1}: {df_test_sample.iloc[i]['Consumer complaint narrative']}")
    print(f"Predicted Category: {df_test_sample.iloc[i]['Predicted Category']}\n")

# Adding Predicted Categories to Original Dataset
df["Predicted Category"] = model.predict(vectorizer.transform(df["Cleaned_Text"]).toarray())
df["Predicted Category"] = df["Predicted Category"].apply(lambda x: reverse_category_mapping[x + 1])

# Save the new dataset with predicted categories
output_file = "/home/user/Documents/KAIBURR/ML-MODEL-TASK-5/complaints_with_predictions.csv"
df.to_csv(output_file, index=False)
print(f"Updated dataset with predicted categories saved to: {output_file}")
