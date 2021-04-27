import React, { FC } from "react";

const ScwLinkRenderer: FC<{ href: string }> = ({ href, children }) => {
  return (
    <iframe
      width={300}
      height={169}
      src={href}
      title={children?.toString()}
      frameBorder={0}
      allow="accelerometer; encrypted-media; gyroscope; picture-in-picture"
      allowFullScreen={true}
    />
  );
};

export default ScwLinkRenderer;
