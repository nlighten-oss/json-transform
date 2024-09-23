FROM node:18-alpine
ARG PORT=10001

WORKDIR /app
COPY javascript/json-transform/test/build .
COPY javascript/json-transform/test/index.html ./test
COPY javascript/json-transform/package.json .
COPY javascript/json-transform/package-lock.json .
RUN npm ci --only=peer

EXPOSE $PORT
CMD [ "node", "./test/server" ]