# AWS Setup Guide for StockSentry Backend

## Current Issue
The backend is failing with "The security token included in the request is invalid" error. This means your AWS credentials are either:
1. Expired
2. Invalid
3. Don't have the required permissions

## How to Fix

### Option 1: Update AWS Credentials (Recommended)
1. Go to AWS IAM Console
2. Create a new user or update existing user
3. Attach these policies:
   - `AmazonDynamoDBFullAccess` (or more restricted if needed)
   - `AmazonSESFullAccess` (for email notifications)
   - `AmazonSNSFullAccess` (for push notifications)

4. Generate new Access Key and Secret Key
5. Update your `application.properties` or set environment variables

### Option 2: Use Environment Variables (Most Secure)
```bash
export AWS_ACCESS_KEY_ID=your_new_access_key
export AWS_SECRET_ACCESS_KEY=your_new_secret_key
export AWS_REGION=ap-south-1
```

### Option 3: Update application.properties
```properties
aws.accessKeyId=your_new_access_key
aws.secretAccessKey=your_new_secret_key
aws.region=ap-south-1
```

## Test the Fix
1. Restart your backend
2. Check the health endpoint: `GET /health`
3. Look for "aws": "CONNECTED" in the response

## Required AWS Services
- **DynamoDB**: For storing users, watchlists, and alert logs
- **SES**: For email notifications
- **SNS**: For push notifications

## Security Note
Never commit AWS credentials to your code repository. Use environment variables or AWS IAM roles for production.
