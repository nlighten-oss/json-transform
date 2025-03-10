import Modal from "./Modal";
import styles from "./CopyDialog.module.css";
import { useEffect, useRef } from "react";

const CopyDialog = ({
  title,
  open,
  value,
  onRequestClose,
  children,
}: {
  title: string;
  open: boolean;
  value: string;
  onRequestClose: () => any;
  children?: any;
}) => {
  const inputRef = useRef<HTMLInputElement | undefined>(undefined);
  useEffect(() => {
    if (open) {
      inputRef.current?.focus();
      inputRef.current?.select();
    }
  }, [open]);

  if (!open) return null;
  return (
    <Modal onRequestClose={onRequestClose} title={title} contentClassName={styles.content}>
      {children}
      <div className={styles.copy_line}>
        <input ref={inputRef} defaultValue={value} readOnly />
        <button
          className="button button--primary button--sm share-button shadow--lw"
          onClick={() => {
            navigator.clipboard.writeText(value);
          }}
        >
          Copy
        </button>
      </div>
      <div className={styles.buttons}>
        <button className="button button--primary button--sm share-button shadow--lw" onClick={onRequestClose}>
          OK
        </button>
      </div>
    </Modal>
  );
};

export default CopyDialog;
