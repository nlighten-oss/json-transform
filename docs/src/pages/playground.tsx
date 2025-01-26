import {lazy, Suspense} from "react";
import Layout from '@theme/Layout';
import BrowserOnly from "@docusaurus/BrowserOnly";
import ExecutionEnvironment from '@docusaurus/ExecutionEnvironment';
import preventResizeObserverError from "../components/hooks/preventResizeObserverError";

const LazyBrowserLayout = lazy(() => import("../components/TransformerTester"))

const playground = () => {

  if (ExecutionEnvironment.canUseDOM) {
    preventResizeObserverError();
  }
  const fallback = <div>Loading... <div data-loader/></div>;

  return (
    <Layout>
      <div className="container" style={{
        // @ts-ignore
        "--ifm-container-width-xl": "1600px",
        paddingBottom: "10px"
      }}>
        <br/>
        <h1>Playground</h1>
        <p>Here you can test transformers yourself...</p>
        <BrowserOnly fallback={fallback}>
          {() => (
            <Suspense fallback={fallback}>
              <LazyBrowserLayout/>
            </Suspense>
          )}
        </BrowserOnly>
      </div>
    </Layout>
  );
}

export default playground;