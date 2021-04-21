/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import React, { FC } from "react";
import styled from "styled-components";
import { useTranslation } from "react-i18next";
import classNames from "classnames";
import { ScwResult } from "./scw";

const SizedIcon = styled.figure`
  width: 96px;
  height: auto;
  margin: 1rem;
`;

const Card = styled.div`
  height: auto !important;
`;

const Content = styled.div`
  overflow-wrap: break-word;
`;

type Props = {
  result: ScwResult;
  className?: string;
};

const ScwCard: FC<Props> = ({ result, className }) => {
  const [t] = useTranslation("plugins");

  return (
    <Card className={classNames("column", className)}>
      <div className="card">
        <div className="media-left">
          <SizedIcon className="image">
            <img
              src="https://assets-global.website-files.com/5fec9210c1841a81c9c6ce7d/60778ce8fa8ba906ee89b0bf_2021_SCW_ramadan_logo_primary_RGB.svg"
              alt="Secure code warrior icon"
            />
          </SizedIcon>
        </div>
        <div className="card-image">
          <figure className="image">
            <div>{result.videos ? <video src={result.videos[0]} controls={true} /> : null}</div>
          </figure>
        </div>
        <div className="card-content">
          <div className="media">
            <div className="media-content">
              <p>
                <strong>{result.name}</strong>
              </p>
            </div>
          </div>
          <Content className="content">
            <p>{result.description}</p>
            {result.videos && result.videos?.length > 1 ? (
              <>
                <p>{t("scm-scw-plugin.pullRequest.addtionalVideos")}</p>
                {result.videos.map((videoLink, index) => (
                  <a href={videoLink} target="_blank">
                    {"Video " + (index + 1)}
                  </a>
                ))}
              </>
            ) : null}
          </Content>
        </div>
        <footer className="card-footer">
          <a href={result?.url} className="card-footer-item" target="_blank">
            {t("scm-scw-plugin.pullRequest.cardLink")}
          </a>
        </footer>
      </div>
    </Card>
  );
};

export default ScwCard;
