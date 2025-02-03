import { ReactNode, useEffect, useMemo } from "react";
import { createPortal } from "react-dom";

import styles from "./Modal.module.css";

const modalRoot = document.body;

const Modal = ({
                 className,
                 contentClassName,
                 onRequestClose,
                 title,
                 children,
                 hideTitle,
               }: {
  className?: string;
  contentClassName?: string;
  onRequestClose: () => any;
  title: ReactNode;
  children: any;
  hideTitle?: boolean;
}) => {
  const el = useMemo(() => {
    const div = document.createElement("div");
    div.className = styles.overlay;
    return div;
  }, []);

  useEffect(() => {
    if (!modalRoot) return () => {};
    modalRoot.appendChild(el);
    return () => modalRoot.removeChild(el);
  }, [el]);

  return createPortal(
    <div
      className={(className || "") + " " + styles.modal}
      role="dialog"
      aria-labelledby="modal__title"
      aria-describedby="modal__content"
    >
      <div id="modal__title" className={styles.title}>
        <div>{!hideTitle && title}</div>
        <div className={styles.close} onClick={onRequestClose} title="Close">
          <svg focusable="false" viewBox="0 0 24 24" width={24} height={24} fill="currentcolor">
            <path d="M19 6.41 17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
          </svg>
        </div>
      </div>

      <div id="modal__content" className={(contentClassName || "") + " " + styles.content}>
        {children}
      </div>
    </div>,
    el,
  );
};

export default Modal;