FROM node:18 AS build

WORKDIR /app

COPY package.json ./

RUN npm install

COPY . ./

RUN npm run build

# Production
FROM nginx:stable-alpine

WORKDIR /usr/share/nginx/html

RUN rm -rf ./*

COPY --from=build /app/build ./

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
