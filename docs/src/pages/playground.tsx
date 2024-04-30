import Layout from '@theme/Layout';
import TransformerTester from "../components/TransformerTester";

const playground = () => {
    return <Layout>
        <div className="container" style={{
            // @ts-ignore
            "--ifm-container-width-xl": "1600px"
        }}>
            <br/>
            <h1>Playground</h1>
            <p>Here you can test transformers yourself...</p>
            <TransformerTester />
        </div>
    </Layout>
}

export default playground;