import {useEffect} from "react";
import useIsBrowser from "@docusaurus/useIsBrowser";

const preventResizeObserverError = () => {
    const isBrowser = useIsBrowser();
    useEffect(() => {
        if (isBrowser) {
            window.addEventListener('error', e => {
                if (e.message.includes('ResizeObserver loop')) {
                    const resizeObserverErrDiv = document.getElementById(
                        'webpack-dev-server-client-overlay-div'
                    );
                    const resizeObserverErr = document.getElementById(
                        'webpack-dev-server-client-overlay'
                    );
                    if (resizeObserverErr) {
                        resizeObserverErr.setAttribute('style', 'display: none');
                    }
                    if (resizeObserverErrDiv) {
                        resizeObserverErrDiv.setAttribute('style', 'display: none');
                    }
                }
            });
        }
    }, [isBrowser]);
}
export default preventResizeObserverError;