# Stage 1: Build the React application
FROM node:18-alpine as builder

WORKDIR /app

# Copy the entire project first
COPY . .

# Install dependencies and build
RUN npm install
RUN npm run build

# Stage 2: Serve the application from Nginx
FROM nginx:alpine

# Copy build artifacts from the builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

