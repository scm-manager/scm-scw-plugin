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

package com.cloudogu.scw;

import com.cloudogu.scm.review.comment.service.Comment;
import com.cloudogu.scm.review.comment.service.CommentEvent;
import com.cloudogu.scm.review.comment.service.CommentService;
import com.cloudogu.scm.review.comment.service.Location;
import com.cloudogu.scm.review.comment.service.Reply;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.pullrequest.service.PullRequestEvent;
import com.cloudogu.scm.review.pullrequest.service.PullRequestUpdatedEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.HandlerEventType;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PullRequestEventSubscriberTest {

  private static final Repository REPOSITORY = RepositoryTestData.create42Puzzle();

  @Mock
  private CommentService commentService;

  @Mock
  private ScwResultService service;

  @InjectMocks
  private PullRequestEventSubscriber eventSubscriber;

  @Test
  void shouldHandlePullRequestUpdates() {
    PullRequest pr = new PullRequest();
    eventSubscriber.handleEvent(new PullRequestEvent(REPOSITORY, pr, pr, HandlerEventType.CREATE));

    verify(service).checkPullRequestForResults(REPOSITORY, pr);
  }

  @Test
  void shouldNotHandleCommentEvent() {
    PullRequest pr = new PullRequest();
    Comment comment = new Comment();
    eventSubscriber.handleEvent(new CommentEvent(REPOSITORY, pr, comment, comment, HandlerEventType.DELETE));

    verify(commentService, never()).reply(any(), any(), any(), any(), any());
  }

  @Test
  void shouldHandleCommentEvent() {
    String text = "Beware sqli!";
    when(service.checkTextForResults(text))
      .thenReturn(ImmutableSet.of(new ScwResult("url", "name", "desc", ImmutableList.of("some video"))));

    PullRequest pr = new PullRequest("1", "source", "target");
    Comment comment = Comment.createComment("a1", text, "trillian", new Location("readme.md"));
    eventSubscriber.handleEvent(new CommentEvent(REPOSITORY, pr, comment, comment, HandlerEventType.CREATE));

    verify(commentService).reply(eq(REPOSITORY.getNamespace()), eq(REPOSITORY.getName()), eq(pr.getId()), eq(comment.getId()), any(Reply.class));
  }

  @Test
  void shouldNotAddReplyToCommentIfSimilarReplyAlreadyExists() {
    String text = "Beware sqli!";
    when(service.checkTextForResults(text))
      .thenReturn(ImmutableSet.of(new ScwResult("url", "name", "desc", ImmutableList.of("some video"))));

    PullRequest pr = new PullRequest("1", "source", "target");
    Comment comment = Comment.createComment("a1", text, "trillian", new Location("readme.md"));
    Reply reply = new Reply();
    reply.setComment("url");
    comment.addReply(reply);

    // Should not add reply if reply with url already exists
    eventSubscriber.handleEvent(new CommentEvent(REPOSITORY, pr, comment, comment, HandlerEventType.CREATE));

    verify(commentService, never()).reply(eq(REPOSITORY.getNamespace()), eq(REPOSITORY.getName()), eq(pr.getId()), eq(comment.getId()), any(Reply.class));
  }
}
