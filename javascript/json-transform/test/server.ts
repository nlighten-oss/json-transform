import { readFileSync } from "fs";
import { parse as urlParse, type UrlWithParsedQuery } from "url";
import { createServer, type IncomingMessage, type ServerResponse } from "http";
import JsonTransformer from "../src/JsonTransformer";
import { HTMLHeaders, JSONHeaders, parseBody, send, JSONBig } from "./serverUtils";

const PORT = 10002;

process.on("uncaughtException", function (err) {
  console.info("Some unhandled error occurred");
  console.error(err);
  console.info("Stopping server");
  process.exit(1);
});

const api: Record<string, (req: IncomingMessage, res: ServerResponse, url: UrlWithParsedQuery) => Promise<any> | any> =
  {
    "GET /": (req, res) => {
      send(res, 200, HTMLHeaders, readFileSync(__dirname + "/index.html", "utf-8"));
    },
    "POST /api/v1/transform": async (req, res) => {
      const body = await parseBody(req);
      console.log("called with " + JSONBig.stringify(body));
      const t = new JsonTransformer(body.definition);
      try {
        const result = await t.transform(body.input, body.additionalContext);
        console.log("returning as result <" + JSONBig.stringify(result) + ">");
        send(res, 200, JSONHeaders, { result });
      } catch (e: any) {
        send(res, 500, JSONHeaders, { error: e.message ?? e });
      }
    },
  };

const server = createServer((req: IncomingMessage, res: ServerResponse) => {
  const parsedUrl = urlParse(req.url ?? "/", true);
  const key = `${req.method} ${parsedUrl.pathname}`;
  if (api[key]) {
    return api[key](req, res, parsedUrl);
  }
  console.warn("Tried an unknown path: " + req.url);
  send(res, 404, JSONHeaders, { error: "Path not found" });
});
const main = async () => {
  server.listen(PORT, () => {
    console.log(`Server listening on ${PORT}`);
  });
};
main().catch(e => console.error(e));