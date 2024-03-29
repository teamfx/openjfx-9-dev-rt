/*
 * Copyright (C) 2011, 2012 Apple Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY APPLE INC. ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL APPLE INC. OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include "config.h"

#if ENABLE(VIDEO_TRACK)

#include "TextTrackList.h"

#include "EventNames.h"
#include "HTMLMediaElement.h"
#include "InbandTextTrack.h"
#include "InbandTextTrackPrivate.h"
#include "LoadableTextTrack.h"

using namespace WebCore;

TextTrackList::TextTrackList(HTMLMediaElement* element, ScriptExecutionContext* context)
    : TrackListBase(element, context)
{
}

TextTrackList::~TextTrackList()
{
}

unsigned TextTrackList::length() const
{
    return m_addTrackTracks.size() + m_elementTracks.size() + m_inbandTracks.size();
}

int TextTrackList::getTrackIndex(TextTrack* textTrack)
{
    if (is<LoadableTextTrack>(textTrack))
        return downcast<LoadableTextTrack>(*textTrack).trackElementIndex();

    if (textTrack->trackType() == TextTrack::AddTrack)
        return m_elementTracks.size() + m_addTrackTracks.find(textTrack);

    if (textTrack->trackType() == TextTrack::InBand)
        return m_elementTracks.size() + m_addTrackTracks.size() + m_inbandTracks.find(textTrack);

    ASSERT_NOT_REACHED();

    return -1;
}

int TextTrackList::getTrackIndexRelativeToRenderedTracks(TextTrack *textTrack)
{
    // Calculate the "Let n be the number of text tracks whose text track mode is showing and that are in the media element's list of text tracks before track."
    int trackIndex = 0;

    for (auto& elementTrack : m_elementTracks) {
        if (!toTextTrack(elementTrack.get())->isRendered())
            continue;

        if (elementTrack == textTrack)
            return trackIndex;
        ++trackIndex;
    }

    for (auto& addTrack : m_addTrackTracks) {
        if (!toTextTrack(addTrack.get())->isRendered())
            continue;

        if (addTrack == textTrack)
            return trackIndex;
        ++trackIndex;
    }

    for (auto& inbandTrack : m_inbandTracks) {
        if (!toTextTrack(inbandTrack.get())->isRendered())
            continue;

        if (inbandTrack == textTrack)
            return trackIndex;
        ++trackIndex;
    }

    ASSERT_NOT_REACHED();

    return -1;
}

TextTrack* TextTrackList::item(unsigned index) const
{
    // 4.8.10.12.1 Text track model
    // The text tracks are sorted as follows:
    // 1. The text tracks corresponding to track element children of the media element, in tree order.
    // 2. Any text tracks added using the addTextTrack() method, in the order they were added, oldest first.
    // 3. Any media-resource-specific text tracks (text tracks corresponding to data in the media
    // resource), in the order defined by the media resource's format specification.

    if (index < m_elementTracks.size())
        return toTextTrack(m_elementTracks[index].get());

    index -= m_elementTracks.size();
    if (index < m_addTrackTracks.size())
        return toTextTrack(m_addTrackTracks[index].get());

    index -= m_addTrackTracks.size();
    if (index < m_inbandTracks.size())
        return toTextTrack(m_inbandTracks[index].get());

    return 0;
}

TextTrack* TextTrackList::getTrackById(const AtomicString& id)
{
    // 4.8.10.12.5 Text track API
    // The getTrackById(id) method must return the first TextTrack in the
    // TextTrackList object whose id IDL attribute would return a value equal
    // to the value of the id argument.
    for (unsigned i = 0; i < length(); ++i) {
        TextTrack* track = item(i);
        if (track->id() == id)
            return track;
    }

    // When no tracks match the given argument, the method must return null.
    return nullptr;
}

void TextTrackList::invalidateTrackIndexesAfterTrack(TextTrack* track)
{
    Vector<RefPtr<TrackBase>>* tracks = 0;

    if (track->trackType() == TextTrack::TrackElement) {
        tracks = &m_elementTracks;
        for (auto& addTrack : m_addTrackTracks)
            toTextTrack(addTrack.get())->invalidateTrackIndex();
        for (auto& inbandTrack : m_inbandTracks)
            toTextTrack(inbandTrack.get())->invalidateTrackIndex();
    } else if (track->trackType() == TextTrack::AddTrack) {
        tracks = &m_addTrackTracks;
        for (auto& inbandTrack : m_inbandTracks)
            toTextTrack(inbandTrack.get())->invalidateTrackIndex();
    } else if (track->trackType() == TextTrack::InBand)
        tracks = &m_inbandTracks;
    else
        ASSERT_NOT_REACHED();

    size_t index = tracks->find(track);
    if (index == notFound)
        return;

    for (size_t i = index; i < tracks->size(); ++i)
        toTextTrack(tracks->at(index).get())->invalidateTrackIndex();
}

void TextTrackList::append(PassRefPtr<TextTrack> prpTrack)
{
    RefPtr<TextTrack> track = prpTrack;

    if (track->trackType() == TextTrack::AddTrack)
        m_addTrackTracks.append(track);
    else if (is<LoadableTextTrack>(*track)) {
        // Insert tracks added for <track> element in tree order.
        size_t index = downcast<LoadableTextTrack>(*track).trackElementIndex();
        m_elementTracks.insert(index, track);
    } else if (track->trackType() == TextTrack::InBand) {
        // Insert tracks added for in-band in the media file order.
        size_t index = static_cast<InbandTextTrack*>(track.get())->inbandTrackIndex();
        m_inbandTracks.insert(index, track);
    } else
        ASSERT_NOT_REACHED();

    invalidateTrackIndexesAfterTrack(track.get());

    ASSERT(!track->mediaElement() || track->mediaElement() == mediaElement());
    track->setMediaElement(mediaElement());

    scheduleAddTrackEvent(track.release());
}

void TextTrackList::remove(TrackBase* track, bool scheduleEvent)
{
    TextTrack* textTrack = toTextTrack(track);
    Vector<RefPtr<TrackBase>>* tracks = 0;
    if (textTrack->trackType() == TextTrack::TrackElement)
        tracks = &m_elementTracks;
    else if (textTrack->trackType() == TextTrack::AddTrack)
        tracks = &m_addTrackTracks;
    else if (textTrack->trackType() == TextTrack::InBand)
        tracks = &m_inbandTracks;
    else
        ASSERT_NOT_REACHED();

    size_t index = tracks->find(track);
    if (index == notFound)
        return;

    invalidateTrackIndexesAfterTrack(textTrack);

    ASSERT(!track->mediaElement() || track->mediaElement() == element());
    track->setMediaElement(0);

    RefPtr<TrackBase> trackRef = (*tracks)[index];
    tracks->remove(index);

    if (scheduleEvent)
        scheduleRemoveTrackEvent(trackRef.release());
}

bool TextTrackList::contains(TrackBase* track) const
{
    const Vector<RefPtr<TrackBase>>* tracks = 0;
    TextTrack::TextTrackType type = toTextTrack(track)->trackType();
    if (type == TextTrack::TrackElement)
        tracks = &m_elementTracks;
    else if (type == TextTrack::AddTrack)
        tracks = &m_addTrackTracks;
    else if (type == TextTrack::InBand)
        tracks = &m_inbandTracks;
    else
        ASSERT_NOT_REACHED();

    return tracks->find(track) != notFound;
}

EventTargetInterface TextTrackList::eventTargetInterface() const
{
    return TextTrackListEventTargetInterfaceType;
}

#endif
