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
import { HalRepresentation } from "@scm-manager/ui-types";
import ScwCard from "./ScwCard";
import { ScwResult } from "./scw";

type Props = { pullRequest: HalRepresentation };

type ScwResults = {
  results: ScwResult[];
};

const ScwCards: FC<Props> = ({ pullRequest }) => {
  const scwResults = (pullRequest._embedded?.scwResults as ScwResults)?.results;

  if (!scwResults) {
    return null;
  }

  return (
    <div className="columns card-columns is-multiline">
      {scwResults.map((r: ScwResult) => (
        <ScwCard result={r} className={scwResults.length <= 1 ? " is-full" : " is-half"} />
      ))}
    </div>
  );
};

export default ScwCards;
