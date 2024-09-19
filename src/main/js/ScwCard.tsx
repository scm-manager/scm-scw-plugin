/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
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
