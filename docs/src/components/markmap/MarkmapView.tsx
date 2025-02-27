import React, { useRef, useEffect } from "react";
import { IPureNode, loadCSS, loadJS } from "markmap-common";
import { Transformer } from "markmap-lib";
import * as markmap from "markmap-view";
import { IMarkmapOptions } from "markmap-view/dist/types";

// INIT
const transformer = new Transformer();
const { scripts, styles } = transformer.getAssets();
Promise.all([loadCSS(styles), loadJS(scripts, { getMarkmap: () => markmap })]).catch(console.error);

export default function MarkmapView({
  root,
  options,
  ...rest
}: { root: IPureNode; options?: Partial<IMarkmapOptions> } & JSX.IntrinsicElements["svg"]) {
  const refSvg = useRef<SVGSVGElement>();
  const refMm = useRef<markmap.Markmap>();

  useEffect(() => {
    if (refMm.current) {
      return;
    }
    refMm.current = markmap.Markmap.create(refSvg.current, {
      duration: 0,
      scrollForPan: false,
    });
  }, []);

  useEffect(() => {
    const mm = refMm.current;
    if (!mm) return;
    mm.setData(root).then(() => mm.fit());
  }, [refMm.current, root]);

  return <svg {...rest} ref={refSvg} />;
}
