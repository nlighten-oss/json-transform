# npm --prefix ./javascript/json-transform run build:test-server
# docker build -f javascript.test-server.Dockerfile -t json_transform_js_test_server_image .

docker build -f java.test-server.Dockerfile -t json_transform_java_test_server_image .
docker run --rm -p 10000:10000 --name json_transform_java_test_server json_transform_java_test_server_image