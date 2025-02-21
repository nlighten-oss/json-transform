const JS_BASE = process.env.IMPL_JS_BASE ?? "http://localhost:10002";
const JAVA_BASE = process.env.IMPL_JAVA_BASE ?? "http://localhost:10000"

export const ImplUrls = {
  "javascript": `${JS_BASE}/api/v1/transform`,
  "java-gson": `${JAVA_BASE}/api/v1/transform/gson`,
  "java-jackson": `${JAVA_BASE}/api/v1/transform/jackson`,
  "java-jsonorg": `${JAVA_BASE}/api/v1/transform/jsonorg`,
  "java-jsonsmart": `${JAVA_BASE}/api/v1/transform/jsonsmart`,
  "java-pojo": `${JAVA_BASE}/api/v1/transform/pojo`
}

export type Implementation = keyof typeof ImplUrls;

export const ImplsByLang: Record<string, Implementation[]> = {
  "java": [
    "java-gson",
    "java-jackson",
    "java-jsonorg",
    "java-jsonsmart",
    "java-pojo",
  ],
  "javascript": [
    "javascript"
  ],
}